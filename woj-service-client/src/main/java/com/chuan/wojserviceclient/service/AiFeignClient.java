package com.chuan.wojserviceclient.service;

import com.chuan.wojmodel.pojo.dto.problem.ProblemContentDTO;
import com.chuan.wojmodel.pojo.dto.problemSubmit.ProblemSubmitAiDTO;
import feign.Response;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * @author chuan_wxy
 *
 */
@FeignClient(name = "woj-ai-service", path = "/api/ai/inner")
public interface AiFeignClient {
    @PostMapping("/trigger")
    String triggerAnalysis(@RequestBody ProblemSubmitAiDTO problemSubmitDTO);
}
