package com.chuan.wojserviceclient.service;

import com.chuan.wojmodel.pojo.dto.problem.ProblemTagDTO;
import com.chuan.wojmodel.pojo.entity.Problem;
import com.chuan.wojmodel.pojo.entity.ProblemSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "woj-web-service", path = "/api/web/inner")
public interface WebFeignClient {

    @PostMapping("/get/problemsubmit/by/id")
    ProblemSubmit getProblemSubmitById(@RequestParam("problemSubmitId") long problemSubmitId);

    @PostMapping("/get/problem/by/id")
    Problem getProblemById(@RequestParam("problemId") long problemId);

    @PostMapping("/get/problem/tags/by/pid")
    List<String> getProblemTagsByPid(@RequestParam("pid") long pid);
}
