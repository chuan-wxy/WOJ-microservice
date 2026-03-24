package com.chuan.wojaiservice;

import com.chuan.wojmodel.pojo.dto.ai.AiChatRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author chuan_wxy
 *
 */
@SpringBootTest
class WojAiServiceApplicationTests {
    private final String AUTODL_API_URL = "https://u435925-26qz-23b45b97.westd.seetacloud.com:8443/v1/chat/completions";


    @Test
    void contextLoads() {
        String language = "c++";
        String code = "#include <iostrea>\n" +
                "\n" +
                "int main() {\n" +
                "    return 0;\n" +
                "}";
        String judgeResult = "Compilation Error";
        String judgeInfo = null;
        String answer = "#include <iostream>\n" +
                "using namespace std;\n" +
                "\n" +
                "int main() {\n" +
                "    int a+b;\n" +
                "    cin>>a>>b;\n" +
                "    cout<<a+b;\n" +
                "    return 0;\n" +
                "}";

        String prompt = String.format("【代码内容】\n%s\n\n【代码语言】\n%s\n\n【判题结果】\n%s\n\n【判题信息】\n%s\n\n【正确答案】\n%s\n\n请分析错误原因并给出简洁的修复建议。", code, language, judgeResult, judgeInfo, answer);

        // 构造请求体
        AiChatRequest request = AiChatRequest.builder()
                .messages(List.of(
                        new AiChatRequest.Message("system", "你是一个专业的 OJ 判题助教，根据下面的内容，给出优化建议或者错误提示（如果题目没有通过），注意不要直接给出答案。"),
                        new AiChatRequest.Message("user", prompt)
                ))
                .build();

        RestTemplate restTemplate = new RestTemplate();

        try {
            // 发送请求
            ResponseEntity<Map> response = restTemplate.postForEntity(AUTODL_API_URL, request, Map.class);

            // 解析 OpenAI 标准返回格式
            List<Map> choices = (List<Map>) response.getBody().get("choices");
            Map message = (Map) choices.get(0).get("message");
            System.out.println(message.get("content"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
