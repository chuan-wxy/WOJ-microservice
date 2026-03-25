package com.chuan.wojwebservice.controller;

import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.exception.StatusSystemErrorException;
import com.chuan.wojcommon.utils.ResultUtils;
import com.chuan.wojmodel.pojo.dto.problemSubmit.ProblemSubmitAddDTO;
import com.chuan.wojmodel.pojo.dto.problemSubmit.ProblemSubmitAiDTO;
import com.chuan.wojmodel.pojo.entity.Problem;
import com.chuan.wojmodel.pojo.entity.ProblemSubmit;
import com.chuan.wojmodel.pojo.entity.User;
import com.chuan.wojmodel.pojo.vo.problemSubmit.ProblemSubmitVO;
import com.chuan.wojserviceclient.service.AiFeignClient;
import com.chuan.wojserviceclient.service.AiStreamFeignClient;
import com.chuan.wojserviceclient.service.UserFeignClient;
import com.chuan.wojwebservice.mapper.ProblemMapper;
import com.chuan.wojwebservice.mapper.ProblemSubmitMapper;
import com.chuan.wojwebservice.service.ProblemSubmitService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * 代码提交接口
 *
 * @Author: chuan-wxy
 * @Date: 2024/9/11 19:59
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/problem_submit")
public class ProblemSubmitController {
    @lombok.Data
    public static class AnalyzeStreamRequest {
        @NotBlank(message = "submitId不能为空")
        private String submitId;
        private String question;
    }

    @Resource
    UserFeignClient userFeignClient;

    @Resource
    AiStreamFeignClient aiStreamFeignClient;

    @Resource
    ProblemSubmitService problemSubmitService;

    @Resource
    ProblemMapper problemMapper;

    @Resource
    ProblemSubmitMapper problemSubmitMapper;

    @PostMapping("/doSubmit")
    public BaseResponse<ProblemSubmitVO> doSubmit(@Valid @RequestBody ProblemSubmitAddDTO problemSubmitAddDTO,
                                                  HttpServletRequest request) throws StatusFailException, StatusSystemErrorException, IOException, InterruptedException {
        User user = userFeignClient.getLoginUser(request).getData();

        ProblemSubmitVO problemSubmitVO = problemSubmitService.doSubmit(problemSubmitAddDTO, user);

        return ResultUtils.success(problemSubmitVO);
    }

    @PostMapping("/trigger-analysis")
    public BaseResponse<String> triggerAnalysis(@NotBlank @RequestBody String submitId,
                                                HttpServletRequest request) throws StatusFailException {
        User user = userFeignClient.getLoginUser(request).getData();
        problemSubmitService.triggerAnalysis(submitId, user.getId());
        return ResultUtils.success("AI analysis started");
    }


    @PostMapping(value = "/analyze/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamAnalysis(@Valid @RequestBody AnalyzeStreamRequest req) {
        ProblemSubmitAiDTO problemSubmitAiDTO = new ProblemSubmitAiDTO();

        ProblemSubmit problemSubmit = problemSubmitMapper.selectById(Long.parseLong(req.getSubmitId()));

        Problem problem = problemMapper.selectById(problemSubmit.getPid());

        problemSubmitAiDTO.setPid(problemSubmit.getPid());
        problemSubmitAiDTO.setLanguage(problemSubmit.getLanguage());
        problemSubmitAiDTO.setCode(problemSubmit.getCode());
        problemSubmitAiDTO.setJudgeResult(problemSubmit.getJudgeResult());
        problemSubmitAiDTO.setJudgeInfo(problemSubmit.getJudgeInfo());
        problemSubmitAiDTO.setTitle(problem.getTitle());
        problemSubmitAiDTO.setDescription(problem.getDescription());
        problemSubmitAiDTO.setQuestion(req.getQuestion());

        SseEmitter emitter = new SseEmitter(120000L);

        CompletableFuture.runAsync(() -> {
            try (feign.Response response = aiStreamFeignClient.triggerAnalysisStream(problemSubmitAiDTO)) {
                if (response.status() != 200) {
                    emitter.send(SseEmitter.event().name("error").data("AI服务异常: " + response.reason()));
                    emitter.complete();
                    return;
                }

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.body().asInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String trimmed = line.trim();
                        if (trimmed.isEmpty()) {
                            continue;
                        }
                        if (trimmed.startsWith("data:")) {
                            String payload = trimmed.substring(5).trim();
                            if (!payload.isEmpty()) {
                                emitter.send(SseEmitter.event().data(payload));
                            }
                        }
                    }
                    emitter.complete();
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

}
