package com.chuan.wojjudgeservice.controller.inner;

import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojjudgeservice.service.JudgeService;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeResponse;
import com.chuan.wojserviceclient.service.JudgeFeignClient;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/inner")
public class JudgeInnerController implements JudgeFeignClient {

    @Resource
    private JudgeService judgeService;

    @Override
    @PostMapping("/dojudge")
    public ExecuteCodeResponse doJudge(@RequestParam("problemSubmitId") long problemSubmitId) throws StatusFailException, IOException, InterruptedException {
        ExecuteCodeResponse executeCodeResponse = judgeService.doJudge(problemSubmitId);
        return executeCodeResponse;
    }
}
