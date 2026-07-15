package com.chuan.wojwebservice.controller.inner;

import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojmodel.pojo.dto.judge.JudgeContextDTO;
import com.chuan.wojmodel.pojo.entity.Problem;
import com.chuan.wojmodel.pojo.entity.ProblemSubmit;
import com.chuan.wojserviceclient.service.WebFeignClient;
import com.chuan.wojwebservice.service.ProblemService;
import com.chuan.wojwebservice.service.ProblemSubmitService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/inner")
public class WebInnerController implements WebFeignClient {

    @Resource
    private ProblemSubmitService problemSubmitService;

    @Resource
    private ProblemService problemService;

    @Override
    @PostMapping("/get/problemsubmit/by/id")
    public ProblemSubmit getProblemSubmitById(@RequestParam("problemSubmitId") long problemSubmitId) {
        return problemSubmitService.getById(problemSubmitId);
    }

    @Override
    @PostMapping("/get/judge/context")
    public JudgeContextDTO getJudgeContext(@RequestParam("problemSubmitId") long problemSubmitId) {
        ProblemSubmit problemSubmit = problemSubmitService.getById(problemSubmitId);
        if (problemSubmit == null || problemSubmit.getPid() == null) {
            return null;
        }

        Problem problem = problemService.getById(problemSubmit.getPid());
        if (problem == null) {
            return null;
        }

        JudgeContextDTO judgeContextDTO = new JudgeContextDTO();
        judgeContextDTO.setProblemSubmitId(problemSubmit.getId());
        judgeContextDTO.setProblemId(problem.getId());
        judgeContextDTO.setProblemCode(problem.getProblemId());
        judgeContextDTO.setUserId(problemSubmit.getUid());
        judgeContextDTO.setLanguage(problemSubmit.getLanguage());
        judgeContextDTO.setCode(problemSubmit.getCode());
        judgeContextDTO.setTimeLimit(problem.getTimeLimit());
        judgeContextDTO.setMemoryLimit(problem.getMemoryLimit());
        judgeContextDTO.setStackLimit(problem.getStackLimit());
        judgeContextDTO.setJudgeMode(problem.getJudgeMode());
        judgeContextDTO.setSpjCode(problem.getSpjCode());
        judgeContextDTO.setSpjLanguage(problem.getSpjLanguage());
        judgeContextDTO.setIsFileIo(problem.getIsFileIo());
        judgeContextDTO.setIoReadFileName(problem.getIoReadFileName());
        judgeContextDTO.setIoWriteFileName(problem.getIoWriteFileName());
        return judgeContextDTO;
    }

    @Override
    @PostMapping("/get/problem/by/id")
    public Problem getProblemById(@RequestParam("problemId") long problemId) {
        return problemService.getById(problemId);
    }

    @Override
    @PostMapping("/get/problem/tags/by/pid")
    public List<String> getProblemTagsByPid(@RequestParam("pid") long pid) {
        try {
            return problemService.getProblemTagsByPid(pid).getData();
        } catch (StatusFailException e) {
            return List.of();
        }
    }
}
