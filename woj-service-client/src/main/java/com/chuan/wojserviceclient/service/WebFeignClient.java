package com.chuan.wojserviceclient.service;

import com.chuan.wojmodel.pojo.entity.ProblemSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "woj-web-service", path = "/api/web/inner")
public interface WebFeignClient {

    @PostMapping("/get/problemsubmit/by/id")
    ProblemSubmit getProblemSubmitById(@RequestParam("problemSubmitId") long problemSubmitId);

    @PostMapping("/update/problemsubmit/by/id")
    boolean updateProblemSubmitById(@RequestBody ProblemSubmit problemSubmitUpdate);
}
