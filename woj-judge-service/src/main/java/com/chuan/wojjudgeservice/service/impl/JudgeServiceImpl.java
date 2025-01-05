package com.chuan.wojjudgeservice.service.impl;

import cn.hutool.json.JSONUtil;
import com.chuan.wojcommon.common.enums.ProblemSubmitStateEnum;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojjudgeservice.codesandbox.CodeSandbox;
import com.chuan.wojjudgeservice.codesandbox.CodeSandboxFactory;
import com.chuan.wojjudgeservice.service.JudgeService;
import com.chuan.wojjudgeservice.service.ProblemUpdateService;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeRequest;
import com.chuan.wojmodel.pojo.dto.problemSubmit.JudgeInfo;
import com.chuan.wojmodel.pojo.entity.Problem;
import com.chuan.wojmodel.pojo.entity.ProblemSubmit;
import com.chuan.wojmodel.pojo.vo.problemSubmit.ProblemSubmitVO;
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
    ProblemUpdateService problemService;

    @Override
    public ProblemSubmitVO doJudge(long problemSubmitId) throws StatusFailException, IOException, InterruptedException {
        ProblemSubmit problemSubmit = webFeignClient.getProblemSubmitById(problemSubmitId);
        if(problemSubmit == null){
            throw new StatusFailException("提交信息不存在");
        }
        Long questionId = problemSubmit.getPid();
        Problem problem = problemService.getById(questionId);
        // 更新状态
        ProblemSubmit problemSubmitUpdate = new ProblemSubmit();
        problemSubmitUpdate.setId(problemSubmitId);
        problemSubmitUpdate.setState(ProblemSubmitStateEnum.RUNING.getValue());

        boolean update = webFeignClient.updateProblemSubmitById(problemSubmitUpdate);

        if(!update){
            throw new StatusFailException("题目状态更新失败");
        }
        String language = problemSubmit.getLanguage();
        String code = problemSubmit.getCode();
        CodeSandbox codeSandbox = CodeSandboxFactory.newCodeSandbox(language);
//        codeSandbox = new CodeSandboxProxy(codeSandbox);
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .build();
        //debug
        System.out.println("沙箱开始");
        JudgeInfo judgeInfo  = codeSandbox.executeCode(executeCodeRequest,problem);
        //debug
        System.out.println("判题返回结果");
        System.out.println(judgeInfo);

        problemSubmitUpdate = new ProblemSubmit();
        problemSubmitUpdate.setId(problemSubmitId);
        problemSubmitUpdate.setState(ProblemSubmitStateEnum.SUCCEED.getValue());
        problemSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = webFeignClient.updateProblemSubmitById(problemSubmitUpdate);
        if(!update){
            throw new StatusFailException("题目状态更新失败");
        }
        ProblemSubmit problemSubmitResult = webFeignClient.getProblemSubmitById(problemSubmitId);
        ProblemSubmitVO problemSubmitVO = new ProblemSubmitVO();

        problemSubmitVO.setId(problemSubmitResult.getId());
        problemSubmitVO.setLanguage(problemSubmitResult.getLanguage());
        problemSubmitVO.setCode(problemSubmitResult.getCode());
        problemSubmitVO.setJudgeInfo(JSONUtil.toBean(problemSubmitResult.getJudgeInfo(),JudgeInfo.class));
        problemSubmitVO.setState(problemSubmitResult.getState());
        problemSubmitVO.setPid(problemSubmitResult.getPid());
        problemSubmitVO.setUid(problemSubmitResult.getUid());

        return problemSubmitVO;
    }
}
