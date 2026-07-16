package com.chuan.wojjudgeservice.codesandbox.impl;

import com.chuan.wojcommon.exception.StatusSystemErrorException;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeRequest;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeResponse;
import com.chuan.wojmodel.pojo.entity.Problem;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JavaCodeSandBox extends JavaCodeSandBoxTemplate {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest, Problem problem) throws IOException, StatusSystemErrorException {
        return super.executeCode(executeCodeRequest, problem);
    }
}
