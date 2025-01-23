package com.chuan.wojwebservice.service.problemSubmit.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuan.wojcommon.common.enums.ProblemSubmitLanguageEnum;
import com.chuan.wojcommon.constant.ProblemSubmitResult;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.exception.StatusSystemErrorException;
import com.chuan.wojmodel.pojo.codesandbox.ExecuteCodeResponse;
import com.chuan.wojmodel.pojo.dto.problemSubmit.ProblemSubmitAddDTO;
import com.chuan.wojmodel.pojo.entity.Problem;
import com.chuan.wojmodel.pojo.entity.ProblemInformation;
import com.chuan.wojmodel.pojo.entity.ProblemSubmit;
import com.chuan.wojmodel.pojo.entity.User;
import com.chuan.wojmodel.pojo.vo.problemSubmit.ProblemSubmitVO;
import com.chuan.wojserviceclient.service.JudgeFeignClient;
import com.chuan.wojwebservice.mapper.ProblemInformationMapper;
import com.chuan.wojwebservice.mapper.ProblemSubmitMapper;
import com.chuan.wojwebservice.service.problem.ProblemService;
import com.chuan.wojwebservice.service.problemSubmit.ProblemSubmitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

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
    private ProblemInformationMapper problemInformationMapper;

    @Autowired
    private JudgeFeignClient judgeFeignClient;

    @Override
    public ProblemSubmitVO doQuestionSubmit(ProblemSubmitAddDTO problemSubmitAddDTO, User user) throws StatusFailException, StatusSystemErrorException, IOException, InterruptedException {
        if (problemSubmitAddDTO == null || problemSubmitAddDTO.getPid() <= 0) {
            throw new StatusFailException("请求体为空或题目id为空");
        }
        if (problemSubmitAddDTO.getCode() == null || problemSubmitAddDTO.getCode().isEmpty()) {
            throw new StatusFailException("代码不能为空");
        }

        String language = problemSubmitAddDTO.getLanguage();
        long questionId = problemSubmitAddDTO.getPid();

        ProblemSubmitLanguageEnum languageEnum = ProblemSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new StatusFailException("编程语言错误");
        }

        // 判断题目是否存在，根据类别获取实体
        Problem problem = problemService.getById(questionId);
        if (problem == null) {
            throw new StatusFailException("题目不存在");
        }

        ProblemSubmit problemSubmit = new ProblemSubmit();
        BeanUtils.copyProperties(problemSubmitAddDTO, problemSubmit);

        problemSubmit.setUid(user.getUuid().trim());

        boolean save = this.save(problemSubmit);

        long questionSubmitId = problemSubmit.getId();

        if (!save) {
            log.debug("[doQuestionSubmit] 保存problemSubmit失败");
            throw new StatusSystemErrorException("保存失败");
        }
        ExecuteCodeResponse executeCodeResponse = judgeFeignClient.doJudge(questionSubmitId);

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

        QueryWrapper<ProblemInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pid", problemSubmit.getPid());
        ProblemInformation problemInformation = problemInformationMapper.selectOne(queryWrapper);
        problemInformation.setSubnum(problemInformation.getSubnum() + 1);

        if (Objects.equals(executeCodeResponse.getResult(), ProblemSubmitResult.AC)) {
            problemInformation.setAcnum(problemInformation.getAcnum() + 1);
        } else if (Objects.equals(executeCodeResponse.getResult(), ProblemSubmitResult.WA)){
            problemInformation.setWanum(problemInformation.getWanum() + 1);
        } else if(Objects.equals(executeCodeResponse.getResult(), ProblemSubmitResult.TLE)){
            problemInformation.setTlenum(problemInformation.getTlenum() + 1);
        } else if (Objects.equals(executeCodeResponse.getResult(), ProblemSubmitResult.MLE)){
            problemInformation.setMlenum(problemInformation.getMlenum() + 1);
        } else if (Objects.equals(executeCodeResponse.getResult(), ProblemSubmitResult.RE)){
            problemInformation.setRenum(problemInformation.getRenum() + 1);
        } else if (Objects.equals(executeCodeResponse.getResult(), ProblemSubmitResult.CE)){
            problemInformation.setCenum(problemInformation.getCenum() + 1);
        } else if (Objects.equals(executeCodeResponse.getResult(), ProblemSubmitResult.SF)) {
            problemInformation.setSfnum(problemInformation.getSfnum() + 1);
        } else {
            log.error("[doQuestionSubmit] 状态错误");
        }

        problemInformationMapper.update(problemInformation,queryWrapper);

        ProblemSubmitVO problemSubmitVO = new ProblemSubmitVO();
        BeanUtils.copyProperties(problemSubmitUpdate, problemSubmitVO);
        problemSubmitVO.setResult(problemSubmitUpdate.getJudgeResult());

        return problemSubmitVO;
    }
}




