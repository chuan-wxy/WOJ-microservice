package com.chuan.wojaiservice.service;

import com.chuan.wojmodel.pojo.dto.problemSubmit.ProblemSubmitAiDTO;

import java.util.function.Consumer;

/**
 * @author chuan_wxy
 *
 */
public interface AiService {
    String triggerAnalysis(ProblemSubmitAiDTO problemSubmitAiDTO);

    void triggerAnalysisStream(ProblemSubmitAiDTO problemSubmitAiDTO, Consumer<String> chunkConsumer);
}
