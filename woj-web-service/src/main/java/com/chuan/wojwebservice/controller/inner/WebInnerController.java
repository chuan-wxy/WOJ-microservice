package com.chuan.wojwebservice.controller.inner;

import com.chuan.wojmodel.pojo.dto.problem.ProblemTagDTO;
import com.chuan.wojmodel.pojo.entity.Problem;
import com.chuan.wojmodel.pojo.entity.ProblemSubmit;
import com.chuan.wojserviceclient.service.WebFeignClient;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojwebservice.service.ProblemService;
import com.chuan.wojwebservice.service.ProblemSubmitService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Override
    @PostMapping("/get/problem/tags/by/pid")
    public List<String> getProblemTagsByPid(@RequestParam("pid") long pid) {
        try {
            return problemService.getProblemTagsByPid(pid).getData();
        } catch (StatusFailException e) {
            return List.of();
        }
    }
}
