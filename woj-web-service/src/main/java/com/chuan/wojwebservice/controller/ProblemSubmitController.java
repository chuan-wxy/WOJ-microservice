package com.chuan.wojwebservice.controller;

import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.exception.StatusSystemErrorException;
import com.chuan.wojcommon.utils.ResultUtils;
import com.chuan.wojmodel.pojo.dto.problemSubmit.ProblemSubmitAddDTO;
import com.chuan.wojmodel.pojo.dto.problemSubmit.ProblemSubmitAiDTO;
import com.chuan.wojmodel.pojo.entity.User;
import com.chuan.wojmodel.pojo.vo.problemSubmit.ProblemSubmitVO;
import com.chuan.wojserviceclient.service.UserFeignClient;
import com.chuan.wojwebservice.service.ProblemSubmitService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 代码提交接口
 *
 * @Author: chuan-wxy
 * @Date: 2024/9/11 19:59
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/problem_submit")
public class ProblemSubmitController {

    @Resource
    UserFeignClient userFeignClient;

    @Autowired
    ProblemSubmitService problemSubmitService;

    @PostMapping("/doSubmit")
    public BaseResponse<ProblemSubmitVO> doSubmit(@Valid @RequestBody ProblemSubmitAddDTO problemSubmitAddDTO,
                                                  HttpServletRequest request) throws StatusFailException, StatusSystemErrorException, IOException, InterruptedException {
        User user = userFeignClient.getLoginUser(request).getData();

        ProblemSubmitVO problemSubmitVO = problemSubmitService.doSubmit(problemSubmitAddDTO, user);

        return ResultUtils.success(problemSubmitVO);
    }

    @PostMapping("/trigger-analysis")
    public BaseResponse<String> triggerAnalysis(@NotBlank @RequestBody String submitId,
                                                HttpServletRequest request) throws StatusFailException {
        User user = userFeignClient.getLoginUser(request).getData();
        problemSubmitService.triggerAnalysis(submitId, user.getId());
        return ResultUtils.success("AI analysis started");
    }

}
