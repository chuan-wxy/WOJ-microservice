package com.chuan.wojwebservice.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuan.wojcommon.common.enums.ProblemSubmitLanguageEnum;
import com.chuan.wojcommon.common.enums.ProblemSubmitStateEnum;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.exception.StatusSystemErrorException;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeResponse;
import com.chuan.wojmodel.pojo.dto.problemSubmit.ProblemSubmitAddDTO;
import com.chuan.wojmodel.pojo.dto.problemSubmit.ProblemSubmitAiDTO;
import com.chuan.wojmodel.pojo.entity.Problem;
import com.chuan.wojmodel.pojo.entity.ProblemSubmit;
import com.chuan.wojmodel.pojo.entity.User;
import com.chuan.wojmodel.pojo.vo.problemSubmit.ProblemSubmitVO;
import com.chuan.wojserviceclient.service.AiFeignClient;
import com.chuan.wojserviceclient.service.JudgeFeignClient;
import com.chuan.wojwebservice.mapper.ProblemMapper;
import com.chuan.wojwebservice.mapper.ProblemStatsMapper;
import com.chuan.wojwebservice.mapper.ProblemSubmitMapper;
import com.chuan.wojwebservice.service.ProblemService;
import com.chuan.wojwebservice.service.ProblemSubmitService;
import com.chuan.wojwebservice.websocket.JudgeWebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author chuan-wxy
 * @description problem_submit service implementation
 */
@Service
@Slf4j
public class ProblemSubmitServiceImpl extends ServiceImpl<ProblemSubmitMapper, ProblemSubmit>
        implements ProblemSubmitService {

    private static final String WS_TYPE_JUDGE_RESULT = "JUDGE_RESULT";
    private static final String WS_TYPE_ERROR = "ERROR";
    private static final String WS_TYPE_AI_START = "AI_ANALYSIS_START";
    private static final String WS_TYPE_AI_CHUNK = "AI_ANALYSIS_CHUNK";
    private static final String WS_TYPE_AI_DONE = "AI_ANALYSIS_DONE";
    private static final String WS_TYPE_AI_ERROR = "AI_ANALYSIS_ERROR";

    @Autowired
    private ProblemService problemService;

    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private ProblemStatsMapper problemStatsMapper;

    @Autowired
    private JudgeFeignClient judgeFeignClient;

    @Autowired
    private AiFeignClient aiFeignClient;

    @Autowired
    private ProblemSubmitMapper problemSubmitMapper;

    @Value("${ai.typewriter.chunk-size:2}")
    private int typewriterChunkSize;

    @Value("${ai.typewriter.delay-ms:35}")
    private long typewriterDelayMs;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProblemSubmitVO doSubmit(ProblemSubmitAddDTO problemSubmitAddDTO, User user)
            throws StatusFailException, StatusSystemErrorException, IOException, InterruptedException {
        String language = problemSubmitAddDTO.getLanguage();
        String code = problemSubmitAddDTO.getCode();
        String pidStr = problemSubmitAddDTO.getPid();

        Long pid;
        try {
            pid = Long.parseLong(pidStr);
        } catch (NumberFormatException e) {
            throw new StatusFailException("ID格式错误，必须为数字");
        }

        ProblemSubmitLanguageEnum languageEnum = ProblemSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new StatusFailException("编程语言错误");
        }

        Problem problem = problemService.getById(pid);
        if (problem == null) {
            throw new StatusFailException("题目不存在");
        }

        ProblemSubmit problemSubmit = new ProblemSubmit();
        problemSubmit.setPid(pid);
        problemSubmit.setCode(code);
        problemSubmit.setLanguage(language);
        problemSubmit.setUid(user.getId());
        problemSubmit.setState(ProblemSubmitStateEnum.PENDING.getCode());

        if (!this.save(problemSubmit)) {
            throw new StatusSystemErrorException("题目提交失败");
        }

        long submitId = problemSubmit.getId();
        Long userId = user.getId();

        CompletableFuture.runAsync(() -> {
            try {
                log.info("start async judge, submitId={}", submitId);
                ExecuteCodeResponse response = judgeFeignClient.doJudge(submitId);
                updateResultAndStats(submitId, pid, response);

                Map<String, Object> pushData = new HashMap<>();
                pushData.put("type", WS_TYPE_JUDGE_RESULT);
                pushData.put("submitId", submitId);
                pushData.put("result", response.getResult());
                pushData.put("info", response.getInfo());
                pushData.put("time", response.getTimeList());
                JudgeWebSocketServer.sendMessage(userId, JSONUtil.toJsonStr(pushData));
            } catch (Exception e) {
                log.error("async judge failed", e);
                Map<String, Object> pushData = new HashMap<>();
                pushData.put("type", WS_TYPE_ERROR);
                pushData.put("msg", "判题机异常");
                JudgeWebSocketServer.sendMessage(userId, JSONUtil.toJsonStr(pushData));
            }
        });

        ProblemSubmitVO problemSubmitVO = new ProblemSubmitVO();
        BeanUtils.copyProperties(problemSubmit, problemSubmitVO);
        problemSubmitVO.setJudgeResult(problemSubmit.getJudgeResult());
        return problemSubmitVO;
    }

    @Override
    public void triggerAnalysis(String submitId, Long userId) {
        if (userId == null) {
            return;
        }

        ProblemSubmitAiDTO problemSubmitAiDTO = new ProblemSubmitAiDTO();

        ProblemSubmit problemSubmit = problemSubmitMapper.selectById(Long.parseLong(submitId));

        Problem problem = problemMapper.selectById(problemSubmit.getPid());

        problemSubmitAiDTO.setPid(problemSubmit.getPid());
        problemSubmitAiDTO.setLanguage(problemSubmit.getLanguage());
        problemSubmitAiDTO.setCode(problemSubmit.getCode());
        problemSubmitAiDTO.setJudgeResult(problemSubmit.getJudgeResult());
        problemSubmitAiDTO.setJudgeInfo(problemSubmit.getJudgeInfo());
        problemSubmitAiDTO.setTitle(problem.getTitle());
        problemSubmitAiDTO.setDescription(problem.getDescription());


        CompletableFuture.runAsync(() -> {
            pushSimpleTypeMessage(userId, WS_TYPE_AI_START);
            try {
                String analysis = aiFeignClient.triggerAnalysis(problemSubmitAiDTO);
                pushTypewriterMessage(userId, analysis == null ? "" : analysis);
                pushSimpleTypeMessage(userId, WS_TYPE_AI_DONE);
            } catch (Exception e) {
                log.error("trigger ai analysis failed, userId={}", userId, e);
                pushAiError(userId, "AI分析失败，请稍后重试");
            }
        });
    }

    private void pushTypewriterMessage(Long userId, String fullText) {
        int safeChunkSize = Math.max(1, typewriterChunkSize);
        List<String> chunks = splitByCodePoints(fullText, safeChunkSize);
        for (int i = 0; i < chunks.size(); i++) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", WS_TYPE_AI_CHUNK);
            payload.put("index", i);
            payload.put("chunk", chunks.get(i));
            JudgeWebSocketServer.sendMessage(userId, JSONUtil.toJsonStr(payload));

            if (!sleepTypewriterInterval()) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private boolean sleepTypewriterInterval() {
        if (typewriterDelayMs <= 0) {
            return true;
        }
        try {
            Thread.sleep(typewriterDelayMs);
            return true;
        } catch (InterruptedException e) {
            log.warn("ai typewriter sleep interrupted", e);
            return false;
        }
    }

    private List<String> splitByCodePoints(String text, int chunkCodePoints) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            chunks.add("");
            return chunks;
        }

        int current = 0;
        while (current < text.length()) {
            int end = current;
            int count = 0;
            while (end < text.length() && count < chunkCodePoints) {
                end = text.offsetByCodePoints(end, 1);
                count++;
            }
            chunks.add(text.substring(current, end));
            current = end;
        }
        return chunks;
    }

    private void pushSimpleTypeMessage(Long userId, String type) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", type);
        JudgeWebSocketServer.sendMessage(userId, JSONUtil.toJsonStr(payload));
    }

    private void pushAiError(Long userId, String msg) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", WS_TYPE_AI_ERROR);
        payload.put("msg", msg);
        JudgeWebSocketServer.sendMessage(userId, JSONUtil.toJsonStr(payload));
    }

    private void updateResultAndStats(Long submitId, Long pid, ExecuteCodeResponse response) {
        ProblemSubmit updateRecord = new ProblemSubmit();
        updateRecord.setId(submitId);
        updateRecord.setState(ProblemSubmitStateEnum.ACCEPTED.getCode());
        updateRecord.setJudgeResult(response.getResult());
        updateRecord.setTimeList(JSONUtil.toJsonStr(response.getTimeList()));
        updateRecord.setMemoryList(JSONUtil.toJsonStr(response.getMemoryList()));
        this.updateById(updateRecord);

        updateProblemStatsAtomic(pid, response.getResult());
    }

    private void updateProblemStatsAtomic(Long pid, String result) {
        int rows = problemStatsMapper.incrementStats(pid, result);
        if (rows <= 0) {
            log.error("increment stats failed, pid={} may not exist in problem_stats", pid);
        }
    }
}
