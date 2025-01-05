package com.chuan.wojjudgeservice.controller.inner;

import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojjudgeservice.service.JudgeService;
import com.chuan.wojmodel.pojo.vo.problemSubmit.ProblemSubmitVO;
import com.chuan.wojserviceclient.service.JudgeFeignClient;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/inner")
public class JudgeInnerController implements JudgeFeignClient {

    @Resource
    private JudgeService judgeService;

    @Override
    @PostMapping("/dojudge")
    public ProblemSubmitVO doJudge(@RequestParam("problemSubmitId") long problemSubmitId) throws StatusFailException, IOException, InterruptedException {
        ProblemSubmitVO problemSubmitVO = judgeService.doJudge(problemSubmitId);
        return problemSubmitVO;
    }
}
