package com.chuan.wojjudgeservice.codesandbox.impl;


import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.chuan.wojcommon.constant.ProblemSubmitResult;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.utils.ProcessUtils;
import com.chuan.wojcommon.utils.SystemUtil;
import com.chuan.wojjudgeservice.codesandbox.CodeSandbox;
import com.chuan.wojjudgeservice.codesandbox.CommonCodeSandboxTemplate;
import com.chuan.wojmodel.pojo.ExecuteMessage;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeRequest;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeResponse;
import com.chuan.wojmodel.pojo.entity.Problem;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private static String staticJudgeCasePath;
    private static String staticSubmitCodePath;

    private static final String GLOBAL_CPP_CLASS_NAME = "Main.cpp";

    @PostConstruct
    public void init() {
        staticJudgeCasePath = judgeCasePath;
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
            InterruptedException, StatusFailException {

        long needTime = problem.getTimeLimit();
        String questionId = problem.getProblemId();

        String code = executeCodeRequest.getCode();

        // 1.黑名单校验
        WordTree wordTree = new WordTree();
        wordTree.addWords(blackList);
        FoundWord foundWord = WORD_TREE.matchWord(code);
        if (foundWord != null) {
            System.out.println("包含禁止词：" + foundWord.getFoundWord());
            return new ExecuteCodeResponse(ProblemSubmitResult.DSC,null, null,null,null,"包含禁止词：" + foundWord.getFoundWord());
        }

        // 2.写文件

        String UUID = java.util.UUID.randomUUID().toString();
        String parentPath = staticSubmitCodePath + File.separator + UUID;
        String path = parentPath + File.separator + GLOBAL_CPP_CLASS_NAME;

        File userCodeFile = saveCodeToFile(code, parentPath, GLOBAL_CPP_CLASS_NAME);

        // 3.编译文件
        ExecuteMessage executeMessage = compileFile(parentPath, path);
        System.out.println("编译结果: " + executeMessage);
        if (executeMessage.getErrorMessage() != null)
        {
            // 返回编译错误信息
            return new ExecuteCodeResponse(ProblemSubmitResult.CE,null, null,null,null,executeMessage.getErrorMessage());
        }

        // 4.执行代码
        ExecuteCodeResponse executeCodeResponse = runFile(userCodeFile, parentPath, questionId, needTime);

        // 5. 文件清理
        boolean b = deleteFile(userCodeFile);
        if (!b)
        {
            log.error("deleteFile error, userCodeFilePath = {}", userCodeFile.getAbsolutePath());
        }

        return executeCodeResponse;
    }

    /**
     * 编译文件
     * @param
     * @return
     */
    public ExecuteMessage compileFile(String parentPath, String path) throws IOException
    {
        String shell = SystemUtil.getShell();

        try {
            List<String> cmd = new ArrayList<>();
            cmd.add(shell);
            cmd.add(shell.equals("bash") ? "-c" : "/C");
            cmd.add("g++");
            cmd.add("-std=c++17");
            cmd.add("-o");
            cmd.add(parentPath+File.separator+"Main.exe");
            cmd.add(path);

            ProcessBuilder pb = new ProcessBuilder(cmd);

            // 合并 错误流和标准流
            // pb.redirectErrorStream(true);
            Process compileProcess = pb.start();
            ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
            // 编译失败
            if(executeMessage.getExitValue() != 0){
                executeMessage.setExitValue(1);
                executeMessage.setMessage(ProblemSubmitResult.CE);
                executeMessage.setErrorMessage("编译错误");
            }
            return executeMessage;
        } catch (Exception e) {
            // 未知错误
            ExecuteMessage executeMessage = new ExecuteMessage();
            executeMessage.setExitValue(1);
            executeMessage.setMessage(e.getMessage());
            executeMessage.setErrorMessage("系统错误");
            return executeMessage;
        }
    }

    /**
     * 执行代码
     *
     * @param userCodeFile
     * @return
     */
    public ExecuteCodeResponse runFile(File userCodeFile,String parentPath,String questionId,long needTime) throws IOException
    {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<Long> timeList = new ArrayList<>();
        for (int i = 0; i < 10 ; i++) {
            long startTime = System.currentTimeMillis();
            try {
                ProcessBuilder builder = new ProcessBuilder(parentPath + File.separator + "Main.exe");
                String inputFilePath = String.format(staticJudgeCasePath+File.separator+questionId+File.separator+"test%d.in", i);
                String outputFilePath = String.format(parentPath+File.separator+"test%d.out",i);
                String answerFilePath = String.format(staticJudgeCasePath+File.separator+questionId + File.separator+"test%d.out", i);

                builder.redirectInput(new File(inputFilePath));
                builder.redirectOutput(new File(outputFilePath));

                Process process = builder.start();

                new Thread(() -> {
                    try {
                        Thread.sleep(needTime + 2000);
                        process.destroy();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();

                int exitCode = process.waitFor();
                System.out.println(process.getErrorStream());

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                List<String> outputStrList = new ArrayList<>();
                // 逐行读取
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null)
                {
                    outputStrList.add(compileOutputLine);
                }
                System.out.println(StringUtils.join(outputStrList, "\n"));


                // 分批获取进程的错误输出
                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                // 逐行读取
                List<String> errorOutputStrList = new ArrayList<>();
                // 逐行读取
                String errorCompileOutputLine;
                while ((errorCompileOutputLine = errorBufferedReader.readLine()) != null)
                {
                    errorOutputStrList.add(errorCompileOutputLine);
                }

                System.out.println(StringUtils.join(errorOutputStrList, "\n"));





                long endTime = System.currentTimeMillis();

                long duration = endTime - startTime;
                //判断是否超时
                if (duration > needTime) {
                    executeCodeResponse.setResult(ProblemSubmitResult.TLE);
                    return executeCodeResponse;
                }
                timeList.add(duration);
                
                System.out.println("样例" + i+1 +"运行时间:" + duration);

                BufferedReader br1 = null;
                BufferedReader br2 = null;
                //判断结果是否正确
                try {
                    br1 = new BufferedReader(new FileReader(answerFilePath));
                    br2 = new BufferedReader(new FileReader(outputFilePath));

                    String line1;
                    String line2;

                    while ((line1 = br1.readLine()) != null && (line2 = br2.readLine()) != null) {
                        if (!line1.equals(line2)) {
                            executeCodeResponse.setResult(ProblemSubmitResult.WA);
                            return executeCodeResponse;
                        }
                    }
                    line2 = br2.readLine();
                    // 检查是否两个文件都同时到达了末尾
                    if (line1 != null || line2 != null) {
                        //debug
                        System.out.println("没有同时到达末尾");
                        executeCodeResponse.setResult(ProblemSubmitResult.WA);
                        return executeCodeResponse;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new StatusFailException("其他错误");
                } finally {
                    if (br1 != null) {
                        try {
                            br1.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (br2 != null) {
                        try {
                            br2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException | InterruptedException | StatusFailException e) {
                throw new RuntimeException(e);
            }
        }
        return executeCodeResponse;
    }
}
