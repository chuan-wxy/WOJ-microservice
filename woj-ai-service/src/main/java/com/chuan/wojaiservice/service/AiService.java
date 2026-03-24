package com.chuan.wojaiservice.service;

import com.chuan.wojmodel.pojo.dto.problemSubmit.ProblemSubmitAiDTO;

/**
 * @author chuan_wxy
 *
 */
public interface AiService {
    String triggerAnalysis(ProblemSubmitAiDTO problemSubmitAiDTO);
}
