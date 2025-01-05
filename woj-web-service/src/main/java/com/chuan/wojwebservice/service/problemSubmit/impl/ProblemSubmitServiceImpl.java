package com.chuan.wojwebservice.service.problemSubmit.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuan.wojcommon.common.enums.ProblemSubmitLanguageEnum;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.exception.StatusSystemErrorException;
import com.chuan.wojmodel.pojo.dto.problemSubmit.ProblemSubmitAddDTO;
import com.chuan.wojmodel.pojo.entity.Problem;
import com.chuan.wojmodel.pojo.entity.ProblemSubmit;
import com.chuan.wojmodel.pojo.entity.User;
import com.chuan.wojmodel.pojo.vo.problemSubmit.ProblemSubmitVO;
import com.chuan.wojserviceclient.service.JudgeFeignClient;
import com.chuan.wojwebservice.mapper.ProblemSubmitMapper;
import com.chuan.wojwebservice.service.problem.ProblemService;
import com.chuan.wojwebservice.service.problemSubmit.ProblemSubmitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
* @author lenovo
* @description 针对表【problem_submit】的数据库操作Service实现
* @createDate 2024-09-12 12:39:38
*/
@Service
@Slf4j
public class ProblemSubmitServiceImpl extends ServiceImpl<ProblemSubmitMapper, ProblemSubmit>
    implements ProblemSubmitService {

    @Autowired
    ProblemService problemService;

    @Autowired
    JudgeFeignClient judgeFeignClient;

    @Override
    public ProblemSubmitVO doQuestionSubmit(ProblemSubmitAddDTO problemSubmitAddDTO, User user) throws StatusFailException, StatusSystemErrorException, IOException, InterruptedException {
        if (problemSubmitAddDTO == null || problemSubmitAddDTO.getPid() <= 0) {
            throw new StatusFailException("请求体为空或题目id为空");
        }

        String language = problemSubmitAddDTO.getLanguage();
        long questionId = problemSubmitAddDTO.getPid();

        ProblemSubmitLanguageEnum languageEnum = ProblemSubmitLanguageEnum.getEnumByValue(language);
        if(languageEnum == null){
            throw new StatusFailException("编程语言错误");
        }

        // 判断题目是否存在，根据类别获取实体
        Problem problem = problemService.getById(questionId);
        if (problem == null) {
            throw new StatusFailException("题目不存在");
        }

        ProblemSubmit problemSubmit = new ProblemSubmit();
        BeanUtils.copyProperties(problemSubmitAddDTO,problemSubmit);

        problemSubmit.setUid(user.getUuid().trim());

        boolean save = this.save(problemSubmit);

        long questionSubmitId = problemSubmit.getId();

        if(!save) {
            log.debug("ProblemSubmitServiceImpl----doQuestionSubmit--->保存problemSubmit失败");
            throw new StatusSystemErrorException("保存失败");
        }
        ProblemSubmitVO problemSubmitVO = judgeFeignClient.doJudge(questionSubmitId);
        System.out.println(3);
        problemSubmitVO.setId(questionSubmitId);
        problemSubmitVO.setLanguage(language);
        problemSubmitVO.setPid(questionId);

        return problemSubmitVO;
    }
}




