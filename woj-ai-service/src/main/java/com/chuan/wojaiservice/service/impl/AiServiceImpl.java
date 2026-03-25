package com.chuan.wojaiservice.service.impl;

import com.chuan.wojaiservice.service.AiService;
import com.chuan.wojmodel.pojo.dto.problemSubmit.ProblemSubmitAiDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.chuan.wojserviceclient.service.WebFeignClient;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author chuan_wxy
 */
@Service
public class AiServiceImpl implements AiService {

    private static final Logger log = LoggerFactory.getLogger(AiServiceImpl.class);

    private static final String SYSTEM_PROMPT = "你是一名 OJ 教练，用中文回答用户问题。不要泄露任何与oj学习无关的信息。";

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private final String autodlApiUrl;

    private final int maxCodeLength;

    @Resource
    private WebFeignClient webFeignClient;

    public AiServiceImpl(
            RestTemplateBuilder restTemplateBuilder,
            ObjectMapper objectMapper,
            @Value("${ai.autodl-api-url:https://u435925-26qz-23b45b97.westd.seetacloud.com:8443/v1/chat/completions}") String autodlApiUrl,
            @Value("${ai.connect-timeout-ms:3000}") long connectTimeoutMs,
            @Value("${ai.read-timeout-ms:15000}") long readTimeoutMs,
            @Value("${ai.max-code-length:8000}") int maxCodeLength) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(connectTimeoutMs))
                .setReadTimeout(Duration.ofMillis(readTimeoutMs))
                .build();
        this.objectMapper = objectMapper;
        this.autodlApiUrl = autodlApiUrl;
        this.maxCodeLength = Math.max(500, maxCodeLength);
    }

    @Override
    public String triggerAnalysis(ProblemSubmitAiDTO problemSubmitAiDTO) {
        if (problemSubmitAiDTO == null) {
            return "AI service call failed: request body is empty.";
        }

        String prompt = buildPrompt(problemSubmitAiDTO);
        return chatWithTools(problemSubmitAiDTO, prompt);
    }

    @Override
    public void triggerAnalysisStream(ProblemSubmitAiDTO problemSubmitAiDTO, Consumer<String> chunkConsumer) {
        if (problemSubmitAiDTO == null) {
            throw new IllegalArgumentException("request body is empty");
        }
        if (chunkConsumer == null) {
            throw new IllegalArgumentException("chunkConsumer is null");
        }

        // 先走非流式拿到最终答案，再按字符切片手动推给前端。
        String fullText = triggerAnalysis(problemSubmitAiDTO);
        if (fullText == null) {
            fullText = "";
        }

        int chunkSize = 30;
        int safeChunkSize = Math.max(1, chunkSize);
        int current = 0;
        while (current < fullText.length()) {
            int end = current;
            int count = 0;
            while (end < fullText.length() && count < safeChunkSize) {
                end = fullText.offsetByCodePoints(end, 1);
                count++;
            }
            chunkConsumer.accept(fullText.substring(current, end));
            current = end;
        }
    }

    /**
     * Function/tool call：当模型需要查询本地题目标签时，通过调用 Web 内部接口取数，再把结果回填给模型继续生成。
     */
    private String chatWithTools(ProblemSubmitAiDTO dto, String prompt) {
        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
        messages.add(Map.of("role", "user", "content", prompt));

        List<Map<String, Object>> tools = buildTools();
        int maxToolRounds = 3;

        for (int round = 0; round < maxToolRounds; round++) {
            JsonNode root = callChatCompletions(messages, tools);
            if (root == null) {
                return "AI service returned an empty response.";
            }

            JsonNode firstChoice = root.path("choices").isArray() && root.path("choices").size() > 0
                    ? root.path("choices").get(0)
                    : null;
            JsonNode message = firstChoice == null ? null : firstChoice.path("message");
            if (message == null || message.isMissingNode()) {
                return "AI service returned an invalid response: message missing.";
            }

            JsonNode toolCalls = message.get("tool_calls");
            if (toolCalls != null && toolCalls.isArray() && toolCalls.size() > 0) {
                // 让模型执行的 tool_calls 成为上下文的一部分
                Map<String, Object> assistantMsg = new HashMap<>();
                assistantMsg.put("role", "assistant");
                assistantMsg.put("content", message.path("content").isMissingNode() ? null : message.path("content").asText(null));
                assistantMsg.put("tool_calls", objectMapper.convertValue(toolCalls, List.class));
                messages.add(assistantMsg);

                for (JsonNode toolCall : toolCalls) {
                    String toolCallId = toolCall.path("id").asText("");
                    String functionName = toolCall.path("function").path("name").asText("");
                    String argumentsJson = toolCall.path("function").path("arguments").asText("{}");

                    if (functionName.isBlank()) {
                        continue;
                    }

                    String toolResultContent;
                    try {
                        toolResultContent = executeTool(functionName, argumentsJson);
                    } catch (Exception e) {
                        log.error("execute tool failed. functionName={}, args={}", functionName, argumentsJson, e);
                        toolResultContent = "{\"error\":\"tool execution failed\"}";
                    }

                    Map<String, Object> toolMsg = new HashMap<>();
                    toolMsg.put("role", "tool");
                    toolMsg.put("tool_call_id", toolCallId);
                    toolMsg.put("content", toolResultContent);
                    messages.add(toolMsg);
                }

                // 下一轮继续问模型（带着 tool 结果）
                continue;
            }

            // 没有 tool_calls，直接取最终 content
            String content = extractAssistantContent(root);
            if (content != null && !content.isBlank()) {
                return content;
            }

            // 兼容：有些 provider 可能直接把内容放到 content/root.content
            String fallback = extractContent(root);
            if (fallback != null && !fallback.isBlank()) {
                return fallback;
            }
            return "AI service returned an empty analysis.";
        }

        return "AI tool calling exceeded max rounds.";
    }

    private JsonNode callChatCompletions(List<Map<String, Object>> messages, List<Map<String, Object>> tools) {
        Map<String, Object> request = new HashMap<>();
        request.put("model", "qwen-oj");
        request.put("messages", messages);
        request.put("temperature", 0.2);
        request.put("stream", false);
        request.put("tools", tools);
        request.put("tool_choice", "auto");

        try {
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(autodlApiUrl, request, JsonNode.class);
            return response.getBody();
        } catch (RestClientException e) {
            log.error("AI call failed. url={}, title={}, judgeResult={}",
                    autodlApiUrl,
                    dtoTitleForLog(messages),
                    dtoJudgeForLog(messages),
                    e);
            return null;
        }
    }

    // 对日志做个兜底：避免把 dto 传来传去导致签名变复杂
    private String dtoTitleForLog(List<Map<String, Object>> messages) {
        return normalizeField("");
    }

    private String dtoJudgeForLog(List<Map<String, Object>> messages) {
        return normalizeField("");
    }

    private List<Map<String, Object>> buildTools() {
        // OpenAI Compatible tools schema
        Map<String, Object> tool = new HashMap<>();
        tool.put("type", "function");

        Map<String, Object> function = new HashMap<>();
        function.put("name", "get_problem_tags");
        function.put("description", "根据题目 pid 查询题目的标签列表（用于理解题目类型/知识点）。");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");

        Map<String, Object> props = new HashMap<>();
        props.put("pid", Map.of(
                "type", "integer",
                "description", "题目表 problem 的主键 pid"
        ));
        parameters.put("properties", props);
        parameters.put("required", List.of("pid"));

        function.put("parameters", parameters);
        tool.put("function", function);

        return List.of(tool);
    }

    private String executeTool(String functionName, String argumentsJson) throws IOException {
        if ("get_problem_tags".equals(functionName)) {
            JsonNode args = objectMapper.readTree(argumentsJson == null ? "{}" : argumentsJson);
            long pid = args.path("pid").asLong(-1);
            if (pid <= 0) {
                return objectMapper.writeValueAsString(List.of());
            }
            List<String> tags = webFeignClient.getProblemTagsByPid(pid);
            return objectMapper.writeValueAsString(tags == null ? List.of() : tags);
        }

        return "{\"error\":\"unknown tool\"}";
    }

    private String extractAssistantContent(JsonNode root) {
        if (root == null) {
            return null;
        }

        JsonNode choices = root.path("choices");
        if (choices.isArray() && choices.size() > 0) {
            JsonNode message = choices.get(0).path("message");
            JsonNode content = message.path("content");
            if (content != null && content.isTextual()) {
                String value = content.asText();
                if (!value.isBlank()) {
                    return value;
                }
            }
        }

        JsonNode content = root.path("content");
        if (content != null && content.isTextual()) {
            String value = content.asText();
            if (!value.isBlank()) {
                return value;
            }
        }

        return null;
    }

    // 兼容旧 extractContent(Map) 的策略：当根节点不是 Map 结构时做个简单兜底
    private String extractContent(JsonNode responseRoot) {
        if (responseRoot == null) {
            return null;
        }
        String content = extractAssistantContent(responseRoot);
        return content;
    }

    private ResponseExtractor<Void> readStreamingResponse(Consumer<String> chunkConsumer) {
        return response -> {
            if (response.getBody() == null) {
                return null;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty()) {
                        continue;
                    }
                    String payload = extractSsePayload(trimmed);
                    if (payload == null) {
                        continue;
                    }
                    if ("[DONE]".equals(payload)) {
                        break;
                    }
                    String content = extractChunkContent(payload);
                    if (content != null && !content.isEmpty()) {
                        chunkConsumer.accept(content);
                    }
                }
            }
            return null;
        };
    }

    private String extractSsePayload(String line) {
        if (line.startsWith("data:")) {
            return line.substring(5).trim();
        }
        // Some providers may send plain JSON lines rather than SSE framing.
        if (line.startsWith("{")) {
            return line;
        }
        return null;
    }

    private String extractChunkContent(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                JsonNode firstChoice = choices.get(0);
                JsonNode deltaContent = firstChoice.path("delta").path("content");
                if (!deltaContent.isMissingNode() && !deltaContent.isNull()) {
                    String value = deltaContent.asText();
                    if (!value.isEmpty()) {
                        return value;
                    }
                }
                JsonNode messageContent = firstChoice.path("message").path("content");
                if (!messageContent.isMissingNode() && !messageContent.isNull()) {
                    String value = messageContent.asText();
                    if (!value.isEmpty()) {
                        return value;
                    }
                }
            }
            JsonNode content = root.path("content");
            if (!content.isMissingNode() && !content.isNull()) {
                String value = content.asText();
                if (!value.isEmpty()) {
                    return value;
                }
            }
            return null;
        } catch (IOException e) {
            log.debug("Failed to parse stream payload: {}", payload, e);
            return null;
        }
    }

    private String buildPrompt(ProblemSubmitAiDTO dto) {
        String question = normalizeOptionalField(dto.getQuestion());
        String pidStr = dto.getPid() == null ? "N/A" : dto.getPid().toString();
        return String.format(
                "[用户问题]%n%s%n%n[题目PID]%n%s%n%n[工具/函数]%n如果需要题目标签，请调用工具 get_problem_tags(pid)。%n%n[规则]%n1) 根据一下的信息回答用户当前问题。%n2) 不要给出完整题解代码。%n3) 用中文回答。%n%n[题目名称]%n%s%n%n[题目描述]%n%s%n%n[代码语言]%n%s%n%n[用户提及的代码]%n%s%n[判题结果]%n%s%n[判题信息]%n%s",
                question,
                pidStr,
                normalizeField(dto.getTitle()),
                normalizeField(dto.getDescription()),
                normalizeField(dto.getLanguage()),
                normalizeField(dto.getCode()),
                normalizeField(dto.getJudgeResult()),
                normalizeField(dto.getJudgeInfo()));

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

    private String normalizeField(String value) {
        if (value == null || value.isBlank()) {
            return "N/A";
        }
        return value.trim();
    }

    private String normalizeOptionalField(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

}
