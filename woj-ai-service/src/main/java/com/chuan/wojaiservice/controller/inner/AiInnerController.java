package com.chuan.wojaiservice.controller.inner;

import com.chuan.wojaiservice.service.AiService;
import com.chuan.wojmodel.pojo.dto.problemSubmit.ProblemSubmitAiDTO;
import com.chuan.wojserviceclient.service.AiFeignClient;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author chuan_wxy
 *
 */
@RestController
@RequestMapping("/inner")
public class AiInnerController implements AiFeignClient {

    @Resource
    AiService aiService;

    @PostMapping("/trigger")
    public String triggerAnalysis(@Valid @RequestBody ProblemSubmitAiDTO problemSubmitAiDTO) {
        return aiService.triggerAnalysis(problemSubmitAiDTO);
    }

}
