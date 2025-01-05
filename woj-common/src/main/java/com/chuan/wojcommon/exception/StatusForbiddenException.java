package com.chuan.wojcommon.exception;

/**
 * @Author: chuan-wxy
 * @Date: 2024/8/16 19:30
 * @Description: 表示一个请求或操作因为某些原因被禁止而触发的异常
 */
public class StatusForbiddenException extends Exception{

    public StatusForbiddenException() {
    }

    public StatusForbiddenException(String message) {
        super(message);
    }

    public StatusForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public StatusForbiddenException(Throwable cause) {
        super(cause);
    }

    public StatusForbiddenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}