package com.chuan.wojjudgeservice.codesandbox.runner;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;

@Component
public class IsolateSandboxRunner implements SandboxRunner {

    private final SandboxRunnerProperties properties;

    private final IsolateMetaParser metaParser;

    public IsolateSandboxRunner(SandboxRunnerProperties properties, IsolateMetaParser metaParser) {
        this.properties = properties;
        this.metaParser = metaParser;
    }

    @Override
    public SandboxRunResult run(SandboxRunRequest request) throws IOException, InterruptedException {
        int boxId = resolveBoxId(request.getWorkDir());
        System.out.println(boxId);
        initBox(boxId);
        try {
            System.out.println(2);
            Path metaPath = Path.of(request.getWorkDir(), request.getMetaFileName() == null ? "isolate.meta" : request.getMetaFileName());
            System.out.println(metaPath);
            List<String> command = buildRunCommand(request, metaPath, boxId);

            long startTime = System.nanoTime();
            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();
            int exitCode = process.waitFor();
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            String output = readOutput(process);
            Map<String, String> meta = metaParser.parse(metaPath);

            return SandboxRunResult.builder()
                    .exitCode(exitCode)
                    .timedOut(isTimedOut(meta))
                    .output(output)
                    .timeMillis(parseSecondsToMillis(meta.get("time"), duration))
                    .memoryKb(parseLong(meta.get("max-rss")))
                    .meta(meta)
                    .build();
        } finally {
            cleanupBox(boxId);
        }
    }

    private List<String> buildRunCommand(SandboxRunRequest request, Path metaPath, int boxId) {
        List<String> command = baseCommand(boxId);
        command.add("--run");
        command.add("--wait");
        command.add("--dir=/box=" + request.getWorkDir() + ":rw");
        if (request.isCompilerEnv()) {
            command.add("--dir=/usr");
            command.add("--dir=/bin");
            command.add("--dir=/lib");
            command.add("--dir=/lib64:maybe");
            command.add("--env=PATH=" + properties.getIsolate().getPathEnv());
        } else {
            command.add("--dir=/lib");
            command.add("--dir=/lib64:maybe");
            command.add("--dir=/usr/lib");
            command.add("--env=PATH=" + properties.getIsolate().getPathEnv());
        }
        command.add("--meta=" + metaPath);
        command.add("--processes=" + Math.max(1, request.getProcessLimit()));
        command.add("--time=" + toSeconds(request.getTimeLimitMillis()));
        command.add("--wall-time=" + toSeconds(request.getWallTimeLimitMillis() > 0
                ? request.getWallTimeLimitMillis()
                : request.getTimeLimitMillis() + 2000));
        command.add("--mem=" + (request.getMemoryLimitKb() == null
                ? properties.getIsolate().getDefaultMemoryLimitKb()
                : request.getMemoryLimitKb()));
        if (request.getStdinPath() != null) {
            command.add("--stdin=" + toBoxPath(request.getWorkDir(), request.getStdinPath()));
        }
        if (request.getStdoutPath() != null) {
            command.add("--stdout=" + toBoxPath(request.getWorkDir(), request.getStdoutPath()));
        }
        if (request.isRedirectErrorStream()) {
            command.add("--stderr-to-stdout");
        } else if (request.getStderrPath() != null) {
            command.add("--stderr=" + toBoxPath(request.getWorkDir(), request.getStderrPath()));
        }
        command.add("--");
        command.addAll(request.getIsolateCommand() == null ? request.getCommand() : request.getIsolateCommand());
        return command;
    }

    private void initBox(int boxId) throws IOException, InterruptedException {
        runControlCommand("--init", boxId);
    }

    private void cleanupBox(int boxId) throws IOException, InterruptedException {
        runControlCommand("--cleanup", boxId);
    }

    private void runControlCommand(String action, int boxId) throws IOException, InterruptedException {
        List<String> command = baseCommand(boxId);
        command.add(action);

        System.out.println(command);
        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
        process.waitFor();
    }

    private List<String> baseCommand(int boxId) {
        List<String> command = new ArrayList<>();
        command.add(properties.getIsolate().getExecutable());
        if (properties.getIsolate().isCg()) {
            command.add("--cg");
        }
        command.add("--box-id=" + boxId);
        return command;
    }

    // todo 优化映射关系，解决冲突
    private int resolveBoxId(String workDir) {
        String normalizedWorkDir = Path.of(workDir).toAbsolutePath().normalize().toString();
        CRC32 crc32 = new CRC32();
        crc32.update(normalizedWorkDir.getBytes(StandardCharsets.UTF_8));
        return (int) (crc32.getValue() % 10000);
    }

    private String toBoxPath(String workDir, String path) {
        Path workPath = Path.of(workDir).toAbsolutePath().normalize();
        Path targetPath = Path.of(path).toAbsolutePath().normalize();
        if (targetPath.startsWith(workPath)) {
            return "/box/" + workPath.relativize(targetPath).toString().replace(File.separatorChar, '/');
        }
        return path;
    }

    private String toSeconds(long millis) {
        if (millis <= 0) {
            return "0";
        }
        return String.format(java.util.Locale.ROOT, "%.3f", millis / 1000.0);
    }

    private boolean isTimedOut(Map<String, String> meta) {
        String status = meta.get("status");
        return status != null && status.toUpperCase().contains("TO");
    }

    private Long parseSecondsToMillis(String value, long fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Math.round(Double.parseDouble(value) * 1000);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String readOutput(Process process) {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        } catch (IOException ignored) {
            return output.toString();
        }
        return output.toString();
    }
}
