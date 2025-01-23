package com.chuan.wojwebservice.controller.inner;

import com.chuan.wojmodel.pojo.entity.Problem;
import com.chuan.wojmodel.pojo.entity.ProblemSubmit;
import com.chuan.wojserviceclient.service.WebFeignClient;
import com.chuan.wojwebservice.service.problem.ProblemService;
import com.chuan.wojwebservice.service.problemSubmit.ProblemSubmitService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inner")
public class WebInnerController implements WebFeignClient {
    @Resource
    private ProblemSubmitService problemSubmitService;

    @Resource
    private ProblemService problemService;

    @Override
    @PostMapping("/get/problemsubmit/by/id")
    public ProblemSubmit getProblemSubmitById(@RequestParam("problemSubmitId")long problemSubmitId) {
        return problemSubmitService.getById(problemSubmitId);
    }

    @Override
    @PostMapping("/get/problem/by/id")
    public Problem getProblemById(@RequestParam("problemId")long problemId) {
        return problemService.getById(problemId);
    }
}
