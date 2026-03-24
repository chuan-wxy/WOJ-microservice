package com.chuan.wojserviceclient.service;

import com.chuan.wojmodel.pojo.dto.problem.ProblemContentDTO;
import com.chuan.wojmodel.pojo.dto.problemSubmit.ProblemSubmitAiDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author chuan_wxy
 *
 */
@FeignClient(name = "woj-ai-service", path = "/api/ai/inner")
public interface AiFeignClient {
    @PostMapping("/trigger")
    String triggerAnalysis(@RequestBody ProblemSubmitAiDTO problemSubmitDTO);

}
