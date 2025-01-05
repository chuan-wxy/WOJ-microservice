package com.chuan.wojjudgeservice.service;

import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojmodel.pojo.vo.problemSubmit.ProblemSubmitVO;

import java.io.IOException;

/**
 * @Author: chuan-wxy
 * @Date: 2024/9/12 13:57
 * @Description:
 */
public interface JudgeService {
    ProblemSubmitVO doJudge(long problemSubmitId) throws StatusFailException, IOException, InterruptedException;
}
