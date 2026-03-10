package com.chuan.wojjudgeservice.service.impl;

import com.chuan.wojcommon.constant.ProblemSubmitResult;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojjudgeservice.codesandbox.impl.CCodeSandBox;
import com.chuan.wojjudgeservice.service.JudgeService;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeRequest;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeResponse;
import com.chuan.wojmodel.pojo.entity.Problem;
import com.chuan.wojmodel.pojo.entity.ProblemSubmit;
import com.chuan.wojserviceclient.service.WebFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Author: chuan-wxy
 * @Date: 2024/9/12 13:57
 * @Description:
 */
@Service
public class JudgeServiceImpl implements JudgeService {

    @Autowired
    WebFeignClient webFeignClient;

    @Autowired
    private CCodeSandBox cCodeSandBox;

    @Override
    public ExecuteCodeResponse doJudge(long problemSubmitId) throws StatusFailException, IOException, InterruptedException {
        ProblemSubmit problemSubmit = webFeignClient.getProblemSubmitById(problemSubmitId);
        if(problemSubmit == null){
            throw new StatusFailException("提交信息不存在");
        }
        Long questionId = problemSubmit.getPid();
        Problem problem = webFeignClient.getProblemById(questionId);
        
        String language = problemSubmit.getLanguage();
        String code = problemSubmit.getCode();

        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .build();

        if (language.equals("c++"))
        {
            return cCodeSandBox.executeCode(executeCodeRequest,problem);
        }
        else
        {
            return new ExecuteCodeResponse(ProblemSubmitResult.DSC,null,null,null,null,"不支持该语言");
        }
    }
}
