package com.chuan.wojjudgeservice.codesandbox.impl;

import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.chuan.wojcommon.constant.ProblemSubmitResult;
import com.chuan.wojcommon.exception.StatusSystemErrorException;
import com.chuan.wojjudgeservice.codesandbox.CodeSandbox;
import com.chuan.wojjudgeservice.codesandbox.CommonCodeSandboxTemplate;
import com.chuan.wojjudgeservice.codesandbox.runner.SandboxRunRequest;
import com.chuan.wojjudgeservice.codesandbox.runner.SandboxRunResult;
import com.chuan.wojjudgeservice.codesandbox.runner.SandboxRunner;
import com.chuan.wojjudgeservice.codesandbox.runner.SandboxRunnerFactory;
import com.chuan.wojmodel.pojo.ExecuteMessage;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeRequest;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeResponse;
import com.chuan.wojmodel.pojo.entity.Problem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Java code sandbox template.
 */
@Slf4j
public abstract class JavaCodeSandBoxTemplate extends CommonCodeSandboxTemplate implements CodeSandbox {

    private static final List<String> BLACK_LIST = Arrays.asList(
            "Runtime",
            "ProcessBuilder",
            "exec",
            "Files",
            "Socket"
    );

    private static final String JAVA_CLASS_NAME = "Main";

    private static final String JAVA_FILE_NAME = JAVA_CLASS_NAME + ".java";

    private static final WordTree WORD_TREE;

    static {
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(BLACK_LIST);
    }

    @Value("${path.code.judgecase-path}")
    private String judgeCasePath;

    @Value("${path.code.submit-code-path}")
    private String submitCodePath;

    @Autowired
    private SandboxRunnerFactory sandboxRunnerFactory;

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest, Problem problem) throws IOException,
            InterruptedException, StatusSystemErrorException {
        long needTime = problem.getTimeLimit();
        String questionId = problem.getProblemId();
        String code = executeCodeRequest.getCode();

        FoundWord foundWord = WORD_TREE.matchWord(code);
        if (foundWord != null) {
            return new ExecuteCodeResponse(ProblemSubmitResult.DSC, "包含禁止词：" + foundWord.getFoundWord(), null, null, null, null);
        }

        String uuid = java.util.UUID.randomUUID().toString();
        String parentPath = submitCodePath + File.separator + uuid;
        String path = parentPath + File.separator + JAVA_FILE_NAME;
        File userCodeFile = saveCodeToFile(code, parentPath, JAVA_FILE_NAME);

        ExecuteMessage executeMessage = compileFile(parentPath, path, problem);
        if (executeMessage.getExitValue() != 0) {
            return new ExecuteCodeResponse(ProblemSubmitResult.CE, executeMessage.getInfo(), null, null, null, null);
        }

        ExecuteCodeResponse executeCodeResponse = runFile(parentPath, questionId, needTime, problem);

//        boolean deleted = deleteFile(userCodeFile);
//        if (!deleted) {
//            log.error("deleteFile error, userCodeFilePath = {}", userCodeFile.getAbsolutePath());
//        }

        return executeCodeResponse;
    }

    public ExecuteMessage compileFile(String parentPath, String path, Problem problem) {
        try {
            SandboxRunner runner = sandboxRunnerFactory.getRunner();
            SandboxRunResult result = runner.run(SandboxRunRequest.builder()
                    .command(List.of("javac", "-encoding", "UTF-8", JAVA_FILE_NAME))
                    .isolateCommand(List.of("/usr/bin/javac", "-encoding", "UTF-8", "/work/" + JAVA_FILE_NAME))
                    .workDir(parentPath)
                    .metaFileName("compile.meta")
                    .timeLimitMillis(5000)
                    .wallTimeLimitMillis(10000)
                    .memoryLimitKb(resolveMemoryLimit(problem))
                    .processLimit(20)
                    .redirectErrorStream(true)
                    .compilerEnv(true)
                    .build());

            ExecuteMessage executeMessage = new ExecuteMessage();
            executeMessage.setExitValue(result.getExitCode());
            executeMessage.setInfo(result.getOutput());
            if (result.getExitCode() != 0) {
                executeMessage.setExitValue(1);
                executeMessage.setResult(ProblemSubmitResult.CE);
            }
            return executeMessage;
        } catch (Exception e) {
            ExecuteMessage executeMessage = new ExecuteMessage();
            executeMessage.setExitValue(1);
            executeMessage.setInfo(e.getMessage());
            return executeMessage;
        }
    }

    public ExecuteCodeResponse runFile(String parentPath, String questionId, long needTime, Problem problem)
            throws IOException, InterruptedException, StatusSystemErrorException {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<Long> timeList = new java.util.ArrayList<>();
        List<Long> memoryList = new java.util.ArrayList<>();

        File judgeCaseDir = new File(judgeCasePath + File.separator + questionId);
        if (!judgeCaseDir.exists() || !judgeCaseDir.isDirectory()) {
            throw new StatusSystemErrorException("测试用例不存在");
        }

        File[] inputFiles = judgeCaseDir.listFiles((dir, name) -> name.startsWith("test") && name.endsWith(".in"));
        if (inputFiles == null || inputFiles.length == 0) {
            throw new StatusSystemErrorException("测试用例不存在");
        }

        Arrays.sort(inputFiles, (f1, f2) -> Integer.compare(extractNumber(f1.getName()), extractNumber(f2.getName())));

        for (File inputFile : inputFiles) {
            String inputFileName = inputFile.getName();
            String baseName = inputFileName.substring(0, inputFileName.lastIndexOf(".in"));

            String inputPath = inputFile.getAbsolutePath();
            String outputPath = parentPath + File.separator + baseName + ".out";
            String answerPath = judgeCaseDir.getAbsolutePath() + File.separator + baseName + ".out";

            if (!new File(answerPath).exists()) {
                throw new StatusSystemErrorException("缺少对应的答案文件：" + answerPath);
            }

            SandboxRunResult result = sandboxRunnerFactory.getRunner().run(SandboxRunRequest.builder()
                    .command(List.of("java", "-cp", parentPath, JAVA_CLASS_NAME))
                    .isolateCommand(List.of("/usr/bin/java", "-cp", "/work", JAVA_CLASS_NAME))
                    .workDir(parentPath)
                    .stdinPath(inputPath)
                    .stdoutPath(outputPath)
                    .metaFileName(baseName + ".run.meta")
                    .timeLimitMillis(needTime)
                    .wallTimeLimitMillis(needTime + 2000)
                    .memoryLimitKb(resolveMemoryLimit(problem))
                    .processLimit(20)
                    .redirectErrorStream(true)
                    .compilerEnv(false)
                    .build());

            if (result.isTimedOut()) {
                log.warn("Java run timeout, input={}, timeLimit={}ms, output={}", inputPath, needTime, result.getOutput());
                executeCodeResponse.setResult(ProblemSubmitResult.TLE);
                executeCodeResponse.setInfo(result.getOutput());
                return executeCodeResponse;
            }

            if (result.getExitCode() != 0) {
                log.warn("Java runtime error, input={}, exitCode={}, output={}",
                        inputPath, result.getExitCode(), result.getOutput());
                executeCodeResponse.setResult(ProblemSubmitResult.RE);
                executeCodeResponse.setInfo(result.getOutput());
                return executeCodeResponse;
            }

            if (!compareAnswer(answerPath, outputPath)) {
                executeCodeResponse.setResult(ProblemSubmitResult.WA);
                return executeCodeResponse;
            }

            timeList.add(result.getTimeMillis());
            memoryList.add(result.getMemoryKb());
        }

        executeCodeResponse.setResult(ProblemSubmitResult.AC);
        executeCodeResponse.setTimeList(timeList);
        executeCodeResponse.setMemoryList(memoryList);
        return executeCodeResponse;
    }

    private Long resolveMemoryLimit(Problem problem) {
        if (problem.getMemoryLimit() == null || problem.getMemoryLimit() <= 0) {
            return null;
        }
        return problem.getMemoryLimit().longValue();
    }

    private int extractNumber(String name) {
        try {
            return Integer.parseInt(name.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean compareAnswer(String answerPath, String outputPath) {
        try (BufferedReader br1 = new BufferedReader(new FileReader(answerPath));
             BufferedReader br2 = new BufferedReader(new FileReader(outputPath))) {

            List<String> lines1 = br1.lines().map(String::stripTrailing).collect(Collectors.toList());
            List<String> lines2 = br2.lines().map(String::stripTrailing).collect(Collectors.toList());

            while (!lines1.isEmpty() && lines1.get(lines1.size() - 1).isEmpty()) {
                lines1.remove(lines1.size() - 1);
            }
            while (!lines2.isEmpty() && lines2.get(lines2.size() - 1).isEmpty()) {
                lines2.remove(lines2.size() - 1);
            }

            if (lines1.size() != lines2.size()) {
                return false;
            }

            for (int i = 0; i < lines1.size(); i++) {
                if (!lines1.get(i).equals(lines2.get(i))) {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            log.error("compare answer failed, answerPath={}, outputPath={}", answerPath, outputPath, e);
            return false;
        }
    }
}
