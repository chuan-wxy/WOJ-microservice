package com.chuan.wojjudgeservice.codesandbox;

import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeRequest;
import com.chuan.wojmodel.pojo.dto.problemSubmit.JudgeInfo;
import com.chuan.wojmodel.pojo.entity.Problem;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class CodeSandboxProxy implements CodeSandbox{
    private final CodeSandbox codeSandbox;

    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }


    @Override
    public JudgeInfo executeCode(ExecuteCodeRequest executeCodeRequest, Problem problem) throws IOException, InterruptedException, StatusFailException {
        log.info("代码沙箱请求信息：" + executeCodeRequest.toString());
        JudgeInfo judgeInfo = codeSandbox.executeCode(executeCodeRequest, problem);
        log.info("代码沙箱返回信息：" + judgeInfo.toString());
        return judgeInfo;
    }

}
