package com.chuan.wojcommon.exception;

/**
 * @Author: chuan-wxy
 * @Date: 2024/8/16 19:30
 * @Description:  用户或进程尝试访问某个资源或执行某个操作时，由于权限不足而触发的异常
 */
public class StatusAccessDeniedException extends Exception {

    public StatusAccessDeniedException() {
    }

    public StatusAccessDeniedException(String message) {
        super(message);
    }

    public StatusAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public StatusAccessDeniedException(Throwable cause) {
        super(cause);
    }

    public StatusAccessDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}