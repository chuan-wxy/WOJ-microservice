package com.chuan.wojjudgeservice.codesandbox.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class LocalSandboxRunner implements SandboxRunner {

    @Override
    public SandboxRunResult run(SandboxRunRequest request) throws IOException, InterruptedException {
        System.out.println(request.toString());

        ProcessBuilder processBuilder = new ProcessBuilder(request.getCommand());
        if (request.getWorkDir() != null) {
            processBuilder.directory(new File(request.getWorkDir()));
        }
        if (request.getEnv() != null) {
            processBuilder.environment().putAll(request.getEnv());
        }
        if (request.getStdinPath() != null) {
            processBuilder.redirectInput(new File(request.getStdinPath()));
        }
        if (request.getStdoutPath() != null) {
            processBuilder.redirectOutput(new File(request.getStdoutPath()));
        }
        if (request.isRedirectErrorStream()) {
            processBuilder.redirectErrorStream(true);
        } else if (request.getStderrPath() != null) {
            processBuilder.redirectError(new File(request.getStderrPath()));
        }

        long startTime = System.nanoTime();
        Process process = processBuilder.start();
        boolean finished = waitFor(process, request.getTimeLimitMillis());
        long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

        if (!finished) {
            process.destroyForcibly();
            log.warn("local sandbox timeout, command={}, workDir={}, duration={}ms",
                    request.getCommand(), request.getWorkDir(), duration);
            return SandboxRunResult.builder()
                    .exitCode(-1)
                    .timedOut(true)
                    .timeMillis(duration)
                    .output(readOutput(process, request))
                    .build();
        }

        return SandboxRunResult.builder()
                .exitCode(process.exitValue())
                .timedOut(false)
                .timeMillis(duration)
                .output(readOutput(process, request))
                .build();
    }

    private boolean waitFor(Process process, long timeLimitMillis) throws InterruptedException {
        if (timeLimitMillis <= 0) {
            process.waitFor();
            return true;
        }
        return process.waitFor(timeLimitMillis, TimeUnit.MILLISECONDS);
    }

    private String readOutput(Process process, SandboxRunRequest request) {
        String redirectedOutput = readRedirectedOutput(request);
        if (!redirectedOutput.isBlank()) {
            return redirectedOutput;
        }

        Charset charset = System.getProperty("os.name").toLowerCase().contains("win")
                ? Charset.forName("GBK")
                : StandardCharsets.UTF_8;
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), charset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        } catch (IOException ignored) {
            return output.toString();
        }
        return output.toString();
    }

    private String readRedirectedOutput(SandboxRunRequest request) {
        if (request.getStdoutPath() == null) {
            return "";
        }
        Path stdoutPath = Path.of(request.getStdoutPath());
        if (!Files.exists(stdoutPath)) {
            return "";
        }
        try {
            return Files.readString(stdoutPath);
        } catch (IOException e) {
            return "";
        }
    }
}
