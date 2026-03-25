package com.chuan.wojaiservice.controller.inner;

import cn.hutool.json.JSONUtil;
import com.chuan.wojaiservice.service.AiService;
import com.chuan.wojmodel.pojo.dto.problemSubmit.ProblemSubmitAiDTO;
import com.chuan.wojserviceclient.service.AiFeignClient;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author chuan_wxy
 *
 */
@RestController
@RequestMapping("/inner")
public class AiInnerController implements AiFeignClient {

    @Resource
    AiService aiService;

    @Override
    @PostMapping("/trigger")
    public String triggerAnalysis(@Valid @RequestBody ProblemSubmitAiDTO problemSubmitAiDTO) {
        return aiService.triggerAnalysis(problemSubmitAiDTO);
    }

    @PostMapping(value = "/trigger/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public StreamingResponseBody triggerAnalysisStream(@Valid @RequestBody ProblemSubmitAiDTO problemSubmitAiDTO) {
        return outputStream -> {
            Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            try {
                aiService.triggerAnalysisStream(problemSubmitAiDTO, chunk ->
                        writeSse(writer, Map.of("type", "chunk", "content", chunk)));
                writeSse(writer, Map.of("type", "done"));
            } catch (Exception e) {
                writeSse(writer, Map.of("type", "error", "message", "AI stream failed: " + e.getMessage()));
            }
        };
    }

    private void writeSse(Writer writer, Map<String, Object> payload) {
        try {
            writer.write("data: ");
            writer.write(JSONUtil.toJsonStr(payload));
            writer.write("\n\n");
            writer.flush();
        } catch (IOException ignored) {
        }
    }

}
