package com.chuan.wojmodel.pojo.dto.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author chuan_wxy
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRequest {
    private String model = "qwen-oj";
    private List<Message> messages;
    private double temperature = 0.2;
    private Boolean stream;

    @Data
    @AllArgsConstructor
    public static class Message {
        private String role; // "system", "user"
        private String content;
    }
}
