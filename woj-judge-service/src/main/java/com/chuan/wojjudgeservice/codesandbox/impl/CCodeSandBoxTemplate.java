package com.chuan.wojjudgeservice.codesandbox.impl;


import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.chuan.wojcommon.constant.ProblemSubmitResult;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.exception.StatusSystemErrorException;
import com.chuan.wojcommon.utils.ProcessUtils;
import com.chuan.wojcommon.utils.SystemUtil;
import com.chuan.wojjudgeservice.codesandbox.CodeSandbox;
import com.chuan.wojjudgeservice.codesandbox.CommonCodeSandboxTemplate;
import com.chuan.wojmodel.pojo.ExecuteMessage;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeRequest;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeResponse;
import com.chuan.wojmodel.pojo.entity.Problem;
import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * c/c++代码沙箱
 */
@Slf4j
public abstract class CCodeSandBoxTemplate extends CommonCodeSandboxTemplate implements CodeSandbox {

    private static final List<String> blackList = Arrays.asList("File", "exec");

    @Value("${path.code.judgecase-path}")
    private String judgeCasePath;

    @Value("${path.code.submit-code-path}")
    private String submitCodePath;

    private static String STATIC_JUDGE_CASE_PATH;
    private static String staticSubmitCodePath;

    private static final String GLOBAL_CPP_CLASS_NAME = "Main.cpp";

    @PostConstruct
    public void init() {
        STATIC_JUDGE_CASE_PATH = judgeCasePath;
        staticSubmitCodePath = submitCodePath;
    }

    private static final WordTree WORD_TREE;
    static
    {
        // 初始化黑名单字典树
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(blackList);
        // 初始安全配置文件路径
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest, Problem problem) throws IOException,
            StatusSystemErrorException {

        long needTime = problem.getTimeLimit();

        String questionId = problem.getProblemId();
        String code = executeCodeRequest.getCode();

        FoundWord foundWord = WORD_TREE.matchWord(code);
        if (foundWord != null) {
            System.out.println("包含禁止词：" + foundWord.getFoundWord());
            return new ExecuteCodeResponse(ProblemSubmitResult.DSC, "包含禁止词：" + foundWord.getFoundWord(),null, null,null,null);
        }

        // 写文件
        String UUID = java.util.UUID.randomUUID().toString();
        String parentPath = staticSubmitCodePath + File.separator + UUID;
        String path = parentPath + File.separator + GLOBAL_CPP_CLASS_NAME;
        File userCodeFile = saveCodeToFile(code, parentPath, GLOBAL_CPP_CLASS_NAME);

        // 编译文件
        ExecuteMessage executeMessage = compileFile(parentPath, path);

        if (executeMessage.getExitValue() !=0 ) {
            // 返回编译错误信息
            return new ExecuteCodeResponse(ProblemSubmitResult.CE, executeMessage.getInfo(), null,null, null, null);
        }

        // 执行代码
        ExecuteCodeResponse executeCodeResponse = runFile(parentPath, questionId, needTime);

        // 文件清理
//        boolean b = deleteFile(userCodeFile);
//        if (!b) {
//            log.error("deleteFile error, userCodeFilePath = {}", userCodeFile.getAbsolutePath());
//        }

        return executeCodeResponse;
    }

    /**
     * 编译文件
     * @param
     * @return
     */
    public ExecuteMessage compileFile(String parentPath, String path) {
        File file = new File(path);
        String fileName = file.getName();  // 只获取文件名 Main.cpp

        String shell = SystemUtil.getShell();

        try {
            List<String> cmd = new ArrayList<>();
            cmd.add(shell);
            cmd.add(shell.equals("bash") ? "-c" : "/C");
            cmd.add("g++");
            cmd.add("-std=c++17");
            cmd.add("-o");
            cmd.add("Main.exe");
            cmd.add(fileName);

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.directory(new File(parentPath));

            // 合并 错误流和标准流
            pb.redirectErrorStream(true);
            Process compileProcess = pb.start();
            ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");

            // 编译失败
            if(executeMessage.getExitValue() != 0){
                executeMessage.setExitValue(1);
                executeMessage.setResult(ProblemSubmitResult.CE);
            }
            return executeMessage;
        } catch (Exception e) {
            // 未知错误
            ExecuteMessage executeMessage = new ExecuteMessage();
            executeMessage.setExitValue(1);
            executeMessage.setInfo(e.getMessage());
            return executeMessage;
        }
    }

    /**
     * 执行代码
     *
     * @param
     * @return
     */
    // todo 实现linux系统下的计时
    public ExecuteCodeResponse runFile(String parentPath,String questionId,long needTime) throws IOException, StatusSystemErrorException {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<Long> timeList = new ArrayList<>();

        // 获取测试用例所在的文件夹
        File judgeCaseDir = new File(STATIC_JUDGE_CASE_PATH + File.separator + questionId);
        if (!judgeCaseDir.exists() || !judgeCaseDir.isDirectory()) {
            throw new StatusSystemErrorException("测试用例不存在");
        }

        // 获取所有以 .in 结尾的文件，并按数字顺序排序
        File[] inputFiles = judgeCaseDir.listFiles((dir, name) -> name.startsWith("test") && name.endsWith(".in"));
        if (inputFiles == null || inputFiles.length == 0) {
            throw new StatusSystemErrorException("测试用例不存在");
        }

        // 排序逻辑：确保 test0.in, test1.in ... test10.in 顺序正确
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
                throw new StatusSystemErrorException(Integer.valueOf("缺失对应的答案文件: {}"), answerPath);
            }

            ProcessBuilder builder = new ProcessBuilder(parentPath + File.separator + "Main.exe");

            builder.redirectInput(new File(inputPath));
            builder.redirectOutput(new File(outputPath));
            builder.redirectErrorStream(true); // 将错误输出合并到标准输出

            long startTime = System.currentTimeMillis();
            Process process = null;

            try {
                process = builder.start();
                /*
                  等待运行，超时控制
                  程序执行到这一行，线程会停在这里
                  如果程序在第规定时间跑完了，这个方法会立刻返回 true
                  如果超时程序还没动静，这个方法会准时返回 false
                 */
                boolean finished = process.waitFor(needTime, TimeUnit.MILLISECONDS);
                long duration = System.currentTimeMillis() - startTime;

                if (!finished) {
                    // 超时强制杀死
                    process.destroyForcibly();
                    executeCodeResponse.setResult(ProblemSubmitResult.TLE);
                    return executeCodeResponse;
                }

                if (process.exitValue() != 0) {
                    // 运行错误（如空指针、溢出）
                    executeCodeResponse.setResult(ProblemSubmitResult.RE);
                    return executeCodeResponse;
                }

                if (!compareAnswer(answerPath, outputPath)) {
                    executeCodeResponse.setResult(ProblemSubmitResult.WA); // Wrong Answer
                    return executeCodeResponse;
                }

                timeList.add(duration);
                //debugger
                System.out.println("样例用时：" + duration);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                if (process != null) {
                    process.destroy();
                }
            }
        }

        executeCodeResponse.setResult(ProblemSubmitResult.AC);
        return executeCodeResponse;
    }

    /**
     * 从文件名中提取数字进行正确排序
     */
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

            // 移除列表末尾的空行（OJ 通用规则）
            while (!lines1.isEmpty() && lines1.get(lines1.size() - 1).isEmpty()) lines1.remove(lines1.size() - 1);
            while (!lines2.isEmpty() && lines2.get(lines2.size() - 1).isEmpty()) lines2.remove(lines2.size() - 1);

            if (lines1.size() != lines2.size()) return false;

            for (int i = 0; i < lines1.size(); i++) {
                if (!lines1.get(i).equals(lines2.get(i))) return false;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

