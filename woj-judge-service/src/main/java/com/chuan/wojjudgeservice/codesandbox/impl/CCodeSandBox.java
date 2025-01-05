package com.chuan.wojjudgeservice.codesandbox.impl;


import cn.hutool.core.io.FileUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.chuan.wojcommon.common.enums.JudgeInfoMessageEnum;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.utils.ProcessUtils;
import com.chuan.wojcommon.utils.SystemUtil;
import com.chuan.wojjudgeservice.codesandbox.CodeSandbox;
import com.chuan.wojmodel.pojo.ExecuteMessage;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeRequest;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeResponse;
import com.chuan.wojmodel.pojo.dto.problemSubmit.JudgeInfo;
import com.chuan.wojmodel.pojo.entity.Problem;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * c/c++代码沙箱
 */
@Slf4j
@Component
public class CCodeSandBox implements CodeSandbox {

    private static final List<String> blackList = Arrays.asList("File", "exec");

    @Value("${path.code.judgecase-path}")
    String judgeCasePath;

    @Value("${path.code.submit-code-path}")
    String submitCodePath;

    static String staticJudgeCasePath;
    static String staticSubmitCodePath;
    @PostConstruct
    public void init() {
        staticJudgeCasePath = judgeCasePath;
        staticSubmitCodePath = submitCodePath;
    }

    @Override
    public JudgeInfo executeCode(ExecuteCodeRequest executeCodeRequest, Problem problem) throws IOException, InterruptedException, StatusFailException {
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        //初始值为ACCEPTED
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());

        long needTime = problem.getTimeLimit();
        String questionId = problem.getProblemId();
        // 写文件
        String code = executeCodeRequest.getCode();
        String UUID = java.util.UUID.randomUUID().toString();
        String parentPath = staticSubmitCodePath + File.separator + UUID;
        String path = parentPath + File.separator + "Main.cpp";

        File file = FileUtil.writeString(code, path, StandardCharsets.UTF_8);
        //黑名单校验
        WordTree wordTree = new WordTree();
        wordTree.addWords(blackList);
        FoundWord foundWord = wordTree.matchWord(code);
        if (foundWord != null) {
            System.out.println("黑名单：" + foundWord.getFoundWord());
            return null;
        }

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
            Process process = pb.start();
            ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(process, "编译");
            if(executeMessage.getExitValue() != 0){
                judgeInfoMessageEnum = JudgeInfoMessageEnum.COMPILE_ERROR;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
        } catch (Exception e) {
            throw new StatusFailException(e.getMessage(), e);
        }

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
                long endTime = System.currentTimeMillis();

                long duration = endTime - startTime;
                //判断是否超时
                if (duration > needTime) {
                    judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEECED;
                    judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                    return judgeInfoResponse;
                }
                timeList.add(duration);

                //debug
                System.out.println("运行时间");
                System.out.println(duration);

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
                            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                            return judgeInfoResponse;
                        }
                    }
                    line2 = br2.readLine();
                    // 检查是否两个文件都同时到达了末尾
                    if (line1 != null || line2 != null) {
                        //debug
                        System.out.println("没有同时到达末尾");
                        judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                        return judgeInfoResponse;
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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        judgeInfoResponse.setTimeList(timeList);
        //清除
        if (file.getParentFile() != null) {
            boolean del = FileUtil.del(parentPath);
            System.out.println("删除" + (del ? "成功" : "失败"));
        }
        return judgeInfoResponse;
    }

    private ExecuteCodeResponse getErrorResponse(Throwable e) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(new ArrayList<>());
        executeCodeResponse.setMessage(e.getMessage());
        // 表示代码沙箱错误
        executeCodeResponse.setStatus(2);
        return executeCodeResponse;
    }

}
