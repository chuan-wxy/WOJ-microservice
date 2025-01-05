package com.chuan.wojwebservice.service.problemSubmit;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.exception.StatusSystemErrorException;
import com.chuan.wojmodel.pojo.dto.problemSubmit.ProblemSubmitAddDTO;
import com.chuan.wojmodel.pojo.entity.ProblemSubmit;
import com.chuan.wojmodel.pojo.entity.User;
import com.chuan.wojmodel.pojo.vo.problemSubmit.ProblemSubmitVO;

import java.io.IOException;

/**
* @author lenovo
* @description 针对表【problem_submit】的数据库操作Service
* @createDate 2024-09-12 12:39:38
*/
public interface ProblemSubmitService extends IService<ProblemSubmit> {
    ProblemSubmitVO doQuestionSubmit(ProblemSubmitAddDTO problemSubmitAddDTO, User user) throws StatusFailException, StatusSystemErrorException, IOException, InterruptedException;

}
