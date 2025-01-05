package com.chuan.wojcommon.exception;

/**
 * @Author: chuan-wxy
 * @Date: 2024/8/16 19:30
 * @Description: 表示服务器或系统内部发生了错误而触发的异常
 */
public class StatusSystemErrorException extends Exception {

    public StatusSystemErrorException() {
    }

    public StatusSystemErrorException(String message) {
        super(message);
    }

    public StatusSystemErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public StatusSystemErrorException(Throwable cause) {
        super(cause);
    }

    public StatusSystemErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}