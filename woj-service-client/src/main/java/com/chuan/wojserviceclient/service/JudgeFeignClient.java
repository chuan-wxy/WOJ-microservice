package com.chuan.wojserviceclient.service;


import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojmodel.pojo.vo.problemSubmit.ProblemSubmitVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

/**
 * @Author: chuan-wxy
 * @Date: 2024/9/12 13:57
 * @Description:
 */
@FeignClient(name = "woj-judge-service", path = "/api/judge/inner")
public interface JudgeFeignClient {
    @PostMapping("/dojudge")
    ProblemSubmitVO doJudge(@RequestParam("problemSubmitId") long problemSubmitId) throws StatusFailException, IOException,
            InterruptedException;
}
