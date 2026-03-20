package com.chuan.wojwebservice.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuan.wojcommon.common.enums.ProblemSubmitLanguageEnum;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.exception.StatusSystemErrorException;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeResponse;
import com.chuan.wojmodel.pojo.dto.problemSubmit.ProblemSubmitAddDTO;
import com.chuan.wojmodel.pojo.entity.Problem;
import com.chuan.wojmodel.pojo.entity.ProblemSubmit;
import com.chuan.wojmodel.pojo.entity.User;
import com.chuan.wojmodel.pojo.vo.problemSubmit.ProblemSubmitVO;
import com.chuan.wojserviceclient.service.JudgeFeignClient;
import com.chuan.wojwebservice.mapper.ProblemSubmitMapper;
import com.chuan.wojwebservice.service.ProblemService;
import com.chuan.wojwebservice.service.ProblemSubmitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
* @author chuan-wxy
* @description 针对表【problem_submit】的数据库操作Service实现
* @createDate 2024-09-12 12:39:38
*/
@Service
@Slf4j
public class ProblemSubmitServiceImpl extends ServiceImpl<ProblemSubmitMapper, ProblemSubmit>
    implements ProblemSubmitService {

    @Autowired
    private ProblemService problemService;

    @Autowired
    private ProblemSubmitMapper problemSubmitMapper;


    @Autowired
    private JudgeFeignClient judgeFeignClient;

    @Override
    public ProblemSubmitVO doQuestionSubmit(ProblemSubmitAddDTO problemSubmitAddDTO, User user) throws StatusFailException, StatusSystemErrorException, IOException, InterruptedException {
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

        // 判断题目是否存在，根据类别获取实体
        Problem problem = problemService.getById(pid);

        if (problem == null) {
            throw new StatusFailException("题目不存在");
        }

        ProblemSubmit problemSubmit = new ProblemSubmit();
        problemSubmit.setPid(pid);
        problemSubmit.setCode(code);
        problemSubmit.setLanguage(language);
        problemSubmit.setUid(user.getId());

        if (!this.save(problemSubmit)) {
            log.debug("[doQuestionSubmit] 保存problemSubmit失败");
            throw new StatusSystemErrorException("题目提交失败");
        }

        long questionSubmitId = problemSubmit.getId();

        ExecuteCodeResponse executeCodeResponse = judgeFeignClient.doJudge(questionSubmitId);

        // todo

        System.out.println("沙箱返回结果：" + executeCodeResponse);

        // 更新状态
        ProblemSubmit problemSubmitUpdate = new ProblemSubmit();
        problemSubmitUpdate.setId(questionSubmitId);
        problemSubmitUpdate.setJudgeResult(executeCodeResponse.getResult());
        problemSubmitUpdate.setTimeList(JSONUtil.toJsonStr(executeCodeResponse.getTimeList()));
        problemSubmitUpdate.setMemoryList(JSONUtil.toJsonStr(executeCodeResponse.getMemoryList()));

        QueryWrapper<ProblemSubmit> ProblemSubmitQueryWrapper = new QueryWrapper<>();
        ProblemSubmitQueryWrapper.eq("id", questionSubmitId);
        problemSubmitMapper.update(problemSubmit, ProblemSubmitQueryWrapper);

        // todo
//        QueryWrapper<ProblemInformation> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("pid", problemSubmit.getPid());
        // ProblemInformation problemInformation = problemInformationMapper.selectOne(queryWrapper);
//        problemInformation.setSubnum(problemInformation.getSubnum() + 1);
//
//        if (Objects.equals(executeCodeResponse.getResult(), ProblemSubmitResult.AC)) {
//            problemInformation.setAcnum(problemInformation.getAcnum() + 1);
//        } else if (Objects.equals(executeCodeResponse.getResult(), ProblemSubmitResult.WA)){
//            problemInformation.setWanum(problemInformation.getWanum() + 1);
//        } else if(Objects.equals(executeCodeResponse.getResult(), ProblemSubmitResult.TLE)){
//            problemInformation.setTlenum(problemInformation.getTlenum() + 1);
//        } else if (Objects.equals(executeCodeResponse.getResult(), ProblemSubmitResult.MLE)){
//            problemInformation.setMlenum(problemInformation.getMlenum() + 1);
//        } else if (Objects.equals(executeCodeResponse.getResult(), ProblemSubmitResult.RE)){
//            problemInformation.setRenum(problemInformation.getRenum() + 1);
//        } else if (Objects.equals(executeCodeResponse.getResult(), ProblemSubmitResult.CE)){
//            problemInformation.setCenum(problemInformation.getCenum() + 1);
//        } else if (Objects.equals(executeCodeResponse.getResult(), ProblemSubmitResult.SF)) {
//            problemInformation.setSfnum(problemInformation.getSfnum() + 1);
//        } else {
//            log.error("[doQuestionSubmit] 状态错误");
//        }
//
//        problemInformationMapper.update(problemInformation,queryWrapper);

        ProblemSubmitVO problemSubmitVO = new ProblemSubmitVO();
        BeanUtils.copyProperties(problemSubmitUpdate, problemSubmitVO);
        problemSubmitVO.setResult(problemSubmitUpdate.getJudgeResult());

        return problemSubmitVO;
    }
}




