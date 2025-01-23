package com.chuan.wojjudgeservice.codesandbox;

import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeResponse;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeRequest;
import com.chuan.wojmodel.pojo.entity.Problem;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 代码沙箱接口定义
 */
@Component
public interface CodeSandbox {

    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest, Problem problem) throws IOException, InterruptedException,
            StatusFailException;
}
