package com.chuan.wojwebservice.controller.inner;

import com.chuan.wojmodel.pojo.entity.ProblemSubmit;
import com.chuan.wojserviceclient.service.WebFeignClient;
import com.chuan.wojwebservice.service.problemSubmit.ProblemSubmitService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inner")
public class WebInnerController implements WebFeignClient {
    @Resource
    private ProblemSubmitService problemSubmitService;

    @Override
    @PostMapping("/get/problemsubmit/by/id")
    public ProblemSubmit getProblemSubmitById(@RequestParam("problemSubmitId")long problemSubmitId) {
        return problemSubmitService.getById(problemSubmitId);
    }

    @Override
    @PostMapping("/update/problemsubmit/by/id")
    public boolean updateProblemSubmitById(@RequestBody ProblemSubmit problemSubmitUpdate) {
        return problemSubmitService.updateById(problemSubmitUpdate);
    }
}
