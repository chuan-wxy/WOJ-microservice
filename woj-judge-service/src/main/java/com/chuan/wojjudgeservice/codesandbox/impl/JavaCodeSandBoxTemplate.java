package com.chuan.wojjudgeservice.codesandbox.impl;

import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.chuan.wojcommon.constant.ProblemSubmitResult;
import com.chuan.wojcommon.exception.StatusSystemErrorException;
import com.chuan.wojcommon.utils.ProcessUtils;
import com.chuan.wojjudgeservice.codesandbox.CodeSandbox;
import com.chuan.wojjudgeservice.codesandbox.CommonCodeSandboxTemplate;
import com.chuan.wojmodel.pojo.ExecuteMessage;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeRequest;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeResponse;
import com.chuan.wojmodel.pojo.entity.Problem;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
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

    private static String staticJudgeCasePath;

    private static String staticSubmitCodePath;

    @PostConstruct
    public void init() {
        staticJudgeCasePath = judgeCasePath;
        staticSubmitCodePath = submitCodePath;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest, Problem problem) throws IOException,
            StatusSystemErrorException {
        long needTime = problem.getTimeLimit();
        String questionId = problem.getProblemId();
        String code = executeCodeRequest.getCode();

        FoundWord foundWord = WORD_TREE.matchWord(code);
        if (foundWord != null) {
            return new ExecuteCodeResponse(ProblemSubmitResult.DSC, "包含禁止词：" + foundWord.getFoundWord(), null, null, null, null);
        }

        String uuid = java.util.UUID.randomUUID().toString();
        String parentPath = staticSubmitCodePath + File.separator + uuid;
        String path = parentPath + File.separator + JAVA_FILE_NAME;
        File userCodeFile = saveCodeToFile(code, parentPath, JAVA_FILE_NAME);

        ExecuteMessage executeMessage = compileFile(parentPath, path);
        if (executeMessage.getExitValue() != 0) {
            return new ExecuteCodeResponse(ProblemSubmitResult.CE, executeMessage.getInfo(), null, null, null, null);
        }

        ExecuteCodeResponse executeCodeResponse = runFile(parentPath, questionId, needTime);

//        boolean deleted = deleteFile(userCodeFile);
//        if (!deleted) {
//            log.error("deleteFile error, userCodeFilePath = {}", userCodeFile.getAbsolutePath());
//        }

        return executeCodeResponse;
    }

    public ExecuteMessage compileFile(String parentPath, String path) {
        File file = new File(path);

        try {
            List<String> cmd = new ArrayList<>();
            cmd.add("javac");
            cmd.add("-encoding");
            cmd.add("UTF-8");
            cmd.add(file.getName());

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.directory(new File(parentPath));
            pb.redirectErrorStream(true);

            Process compileProcess = pb.start();
            ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "Java compile");
            if (executeMessage.getExitValue() != 0) {
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

    public ExecuteCodeResponse runFile(String parentPath, String questionId, long needTime) throws IOException, StatusSystemErrorException {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<Long> timeList = new ArrayList<>();

        File judgeCaseDir = new File(staticJudgeCasePath + File.separator + questionId);
        if (!judgeCaseDir.exists() || !judgeCaseDir.isDirectory()) {
            throw new StatusSystemErrorException("测试用例不存在");
        }

        File[] inputFiles = judgeCaseDir.listFiles((dir, name) -> name.startsWith("test") && name.endsWith(".in"));
        if (inputFiles == null || inputFiles.length == 0) {
            throw new StatusSystemErrorException("测试用例不存在");
        }

        Arrays.sort(inputFiles, (f1, f2) -> {
            int n1 = extractNumber(f1.getName());
            int n2 = extractNumber(f2.getName());
            return Integer.compare(n1, n2);
        });

        for (File inputFile : inputFiles) {
            String inputFileName = inputFile.getName();
            String baseName = inputFileName.substring(0, inputFileName.lastIndexOf(".in"));

            String inputPath = inputFile.getAbsolutePath();
            String outputPath = parentPath + File.separator + baseName + ".out";
            String answerPath = judgeCaseDir.getAbsolutePath() + File.separator + baseName + ".out";

            if (!new File(answerPath).exists()) {
                throw new StatusSystemErrorException("缺少对应的答案文件：" + answerPath);
            }

            ProcessBuilder builder = new ProcessBuilder("java", "-cp", parentPath, JAVA_CLASS_NAME);
            builder.directory(new File(parentPath));
            builder.redirectInput(new File(inputPath));
            builder.redirectOutput(new File(outputPath));
            builder.redirectErrorStream(true);

            long startTime = System.nanoTime();
            Process process = null;

            try {
                process = builder.start();
                boolean finished = process.waitFor(needTime, TimeUnit.MILLISECONDS);
                long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

                if (!finished) {
                    process.destroyForcibly();
                    executeCodeResponse.setResult(ProblemSubmitResult.TLE);
                    return executeCodeResponse;
                }

                if (process.exitValue() != 0) {
                    executeCodeResponse.setResult(ProblemSubmitResult.RE);
                    return executeCodeResponse;
                }

                if (!compareAnswer(answerPath, outputPath)) {
                    executeCodeResponse.setResult(ProblemSubmitResult.WA);
                    return executeCodeResponse;
                }

                timeList.add(duration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new StatusSystemErrorException("Java 程序运行被中断");
            } finally {
                if (process != null) {
                    process.destroy();
                }
            }
        }

        executeCodeResponse.setResult(ProblemSubmitResult.AC);
        executeCodeResponse.setTimeList(timeList);
        return executeCodeResponse;
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
