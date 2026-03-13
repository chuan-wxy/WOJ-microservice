package com.chuan.wojcommon.exception;

import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    /**
     * 处理业务操作失败异常（StatusFailException）
     * 场景：参数合法但业务逻辑校验失败（如唯一键冲突、库存不足）
     */
    @ExceptionHandler(StatusFailException.class)
    public BaseResponse handleStatusFailException(StatusFailException e) {
        // 打印完整异常栈轨迹，方便排查（第二个参数传e，log会自动打印栈轨迹）
        log.error("全局异常捕获 - 业务操作失败：{}", e.getMessage(), e);
        // 兜底：若code为null，默认用400；信息为null则用默认提示
        Integer code = e.getCode() != null ? e.getCode() : 400;
        String message = e.getMessage() != null ? e.getMessage() : "业务操作失败";
        return ResultUtils.error(code, message);
    }

    /**
     * 处理禁止访问异常（StatusForbiddenException）
     * 场景：资源层面禁止访问（如IP拉黑、接口未开放）
     */
    @ExceptionHandler(StatusForbiddenException.class)
    public BaseResponse handleStatusForbiddenException(StatusForbiddenException e) {
        log.error("全局异常捕获 - 禁止访问：{}", e.getMessage(), e);
        Integer code = e.getCode() != null ? e.getCode() : 403;
        String message = e.getMessage() != null ? e.getMessage() : "禁止访问该资源";
        return ResultUtils.error(code, message);
    }

    /**
     * 处理访问被拒绝异常（StatusAccessDeniedException）
     * 场景：用户已登录但无具体操作权限（如普通用户删除他人订单）
     */
    @ExceptionHandler(StatusAccessDeniedException.class)
    public BaseResponse handleStatusAccessDeniedException(StatusAccessDeniedException e) {
        log.error("全局异常捕获 - 访问被拒绝（权限不足）：{}", e.getMessage(), e);
        Integer code = e.getCode() != null ? e.getCode() : 403;
        String message = e.getMessage() != null ? e.getMessage() : "您无此操作权限";
        return ResultUtils.error(code, message);
    }

    /**
     * 处理系统内部错误异常（StatusSystemErrorException）
     * 场景：服务端自身故障（如数据库连接失败、第三方接口调用异常）
     */
    @ExceptionHandler(StatusSystemErrorException.class)
    public BaseResponse handleStatusSystemErrorException(StatusSystemErrorException e) {
        log.error("全局异常捕获 - 系统内部错误：{}", e.getMessage(), e);
        Integer code = e.getCode() != null ? e.getCode() : 500;
        // 系统错误对外隐藏具体原因，返回通用提示（底层详情已打日志）
        String showMessage = "系统繁忙，请稍后重试";
        return ResultUtils.error(code, showMessage);
    }

    /**
     * 处理校验错误
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message;
        if (fieldError != null) {
            message = fieldError.getField() + " " + fieldError.getDefaultMessage();
        } else {
            message = "参数校验失败";
        }
        return ResultUtils.error(400, message);
    }

    /**
     * 处理所有未捕获的通用异常（兜底处理器）
     */
    @ExceptionHandler(Exception.class)
    public BaseResponse handleGenericException(Exception e) {
        log.error("全局异常捕获 - 未知系统异常：", e); // 直接传e，打印完整栈轨迹
        // 通用异常统一返回500，对外返回通用提示
        return ResultUtils.error(500, "服务器内部错误，请联系管理员");
    }
}
