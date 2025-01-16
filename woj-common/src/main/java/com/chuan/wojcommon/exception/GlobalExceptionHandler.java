package com.chuan.wojcommon.exception;

import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常响应器
 *
 * @Author: chuan-wxy
 * @Date: 2024/8/19 18:26
 * @Description:token失效,请重新登陆
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public BaseResponse businessExceptionHandler(Exception e) {
        log.error("全局异常响应器---->Exception:{}", e);
        return ResultUtils.error(e.getMessage());
    }
    @ExceptionHandler(StatusFailException.class)
    public BaseResponse businessExceptionHandler(StatusFailException e) {
        log.error("全局异常响应器---->StatusFailException:{}", e);
        return ResultUtils.error(e.code,e.getMessage());
    }
}
