package com.chuan.wojjudgeservice.service;

import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeResponse;

import java.io.IOException;

/**
 * @Author: chuan-wxy
 * @Date: 2024/9/12 13:57
 * @Description:
 */
public interface JudgeService {
    ExecuteCodeResponse doJudge(long problemSubmitId) throws StatusFailException, IOException, InterruptedException;
}
