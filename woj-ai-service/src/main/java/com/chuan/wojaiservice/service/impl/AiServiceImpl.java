package com.chuan.wojaiservice.service.impl;

import com.chuan.wojaiservice.service.AiService;
import com.chuan.wojmodel.pojo.dto.ai.AiChatRequest;
import com.chuan.wojmodel.pojo.dto.problemSubmit.ProblemSubmitAiDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * @author chuan_wxy
 */
@Service
public class AiServiceImpl implements AiService {

    private static final Logger log = LoggerFactory.getLogger(AiServiceImpl.class);

    private static final String SYSTEM_PROMPT = "You are an OJ assistant. Analyze submissions and provide concise suggestions. Do not output full answer code.";

    private final RestTemplate restTemplate;

    private final String autodlApiUrl;

    private final int maxCodeLength;

    public AiServiceImpl(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${ai.autodl-api-url:https://u435925-26qz-23b45b97.westd.seetacloud.com:8443/}") String autodlApiUrl,
            @Value("${ai.connect-timeout-ms:3000}") long connectTimeoutMs,
            @Value("${ai.read-timeout-ms:15000}") long readTimeoutMs,
            @Value("${ai.max-code-length:8000}") int maxCodeLength) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(connectTimeoutMs))
                .setReadTimeout(Duration.ofMillis(readTimeoutMs))
                .build();
        this.autodlApiUrl = autodlApiUrl;
        this.maxCodeLength = Math.max(500, maxCodeLength);
    }

    @Override
    public String triggerAnalysis(ProblemSubmitAiDTO problemSubmitAiDTO) {
        if (problemSubmitAiDTO == null) {
            return "AI service call failed: request body is empty.";
        }

        String prompt = buildPrompt(problemSubmitAiDTO);
        AiChatRequest request = AiChatRequest.builder()
                .messages(List.of(
                        new AiChatRequest.Message("system", SYSTEM_PROMPT),
                        new AiChatRequest.Message("user", prompt)
                ))
                .build();

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(autodlApiUrl, request, Map.class);
            return extractContent(response.getBody());
        } catch (RestClientException e) {
            log.error("AI call failed. url={}, title={}, judgeResult={}",
                    autodlApiUrl,
                    normalizeField(problemSubmitAiDTO.getTitle()),
                    normalizeField(problemSubmitAiDTO.getJudgeResult()),
                    e);
            return "AI service call failed: " + e.getMessage();
        }
    }

    private String buildPrompt(ProblemSubmitAiDTO dto) {
        return String.format(
                "[User Code]%n%s%n%n[Programming Language]%n%s%n%n[Judge Result]%n%s%n%n[Judge Info]%n%s%n%n[Problem Title]%n%s%n%n[Problem Description]%n%s%n%nPlease analyze the root cause and provide concise fix suggestions without revealing full answer code.",
                limitCodeLength(normalizeField(dto.getCode())),
                normalizeField(dto.getLanguage()),
                normalizeField(dto.getJudgeResult()),
                normalizeField(dto.getJudgeInfo()),
                normalizeField(dto.getTitle()),
                normalizeField(dto.getDescription()));
    }

    private String extractContent(Map<?, ?> responseBody) {
        if (responseBody == null) {
            return "AI service returned an empty response.";
        }

        Object choicesValue = responseBody.get("choices");
        if (!(choicesValue instanceof List<?> choices) || choices.isEmpty()) {
            log.warn("AI response does not include valid choices. body={}", responseBody);
            return "AI service returned an invalid response: choices missing.";
        }

        Object firstChoiceValue = choices.get(0);
        if (!(firstChoiceValue instanceof Map<?, ?> firstChoice)) {
            log.warn("AI response first choice is not a JSON object. value={}", firstChoiceValue);
            return "AI service returned an invalid response: choice format error.";
        }

        Object messageValue = firstChoice.get("message");
        if (!(messageValue instanceof Map<?, ?> message)) {
            log.warn("AI response choice does not include message. choice={}", firstChoice);
            return "AI service returned an invalid response: message missing.";
        }

        Object contentValue = message.get("content");
        if (!(contentValue instanceof String content) || content.isBlank()) {
            log.warn("AI response message does not include content. message={}", message);
            return "AI service returned an empty analysis.";
        }

        return content;
    }

    private String limitCodeLength(String code) {
        if (code.length() <= maxCodeLength) {
            return code;
        }
        int omittedChars = code.length() - maxCodeLength;
        return code.substring(0, maxCodeLength) + String.format("%n%n[Code truncated, omitted %d characters]", omittedChars);
    }

    private String normalizeField(String value) {
        if (value == null || value.isBlank()) {
            return "N/A";
        }
        return value.trim();
    }
}
