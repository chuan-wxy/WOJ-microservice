package com.chuan.wojjudgeservice.service.impl;

import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.exception.StatusSystemErrorException;
import com.chuan.wojjudgeservice.codesandbox.CodeSandbox;
import com.chuan.wojjudgeservice.codesandbox.CodeSandboxFactory;
import com.chuan.wojjudgeservice.service.JudgeService;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeRequest;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeResponse;
import com.chuan.wojmodel.pojo.dto.judge.JudgeContextDTO;
import com.chuan.wojmodel.pojo.entity.Problem;
import com.chuan.wojserviceclient.service.WebFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Judge service implementation.
 */
@Service
public class JudgeServiceImpl implements JudgeService {

    @Autowired
    private WebFeignClient webFeignClient;

    @Autowired
    private CodeSandboxFactory codeSandboxFactory;

    @Override
    public ExecuteCodeResponse doJudge(long problemSubmitId) throws StatusFailException, IOException, InterruptedException, StatusSystemErrorException {
        JudgeContextDTO judgeContextDTO = webFeignClient.getJudgeContext(problemSubmitId);
        if (judgeContextDTO == null) {
            throw new StatusFailException("判题上下文不存在");
        }

        String language = judgeContextDTO.getLanguage();
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(judgeContextDTO.getCode())
                .language(language)
                .build();

        CodeSandbox codeSandbox = codeSandboxFactory.getSandbox(language);
        return codeSandbox.executeCode(executeCodeRequest, buildProblem(judgeContextDTO));
    }

    private Problem buildProblem(JudgeContextDTO judgeContextDTO) {
        Problem problem = new Problem();
        problem.setId(judgeContextDTO.getProblemId());
        problem.setProblemId(judgeContextDTO.getProblemCode());
        problem.setTimeLimit(judgeContextDTO.getTimeLimit());
        problem.setMemoryLimit(judgeContextDTO.getMemoryLimit());
        problem.setStackLimit(judgeContextDTO.getStackLimit());
        problem.setJudgeMode(judgeContextDTO.getJudgeMode());
        problem.setSpjCode(judgeContextDTO.getSpjCode());
        problem.setSpjLanguage(judgeContextDTO.getSpjLanguage());
        problem.setIsFileIo(judgeContextDTO.getIsFileIo());
        problem.setIoReadFileName(judgeContextDTO.getIoReadFileName());
        problem.setIoWriteFileName(judgeContextDTO.getIoWriteFileName());
        return problem;
    }
}
