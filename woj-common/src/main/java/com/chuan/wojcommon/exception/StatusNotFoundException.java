package com.chuan.wojcommon.exception;

/**
 * @Author: chuan-wxy
 * @Date: 2024/8/16 19:30
 * @Description: 表示客户端尝试访问的资源或数据在服务器上不存在或无法找到而触发的异常
 */
public class StatusNotFoundException extends Exception{

    public StatusNotFoundException() {
    }

    public StatusNotFoundException(String message) {
        super(message);
    }

    public StatusNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public StatusNotFoundException(Throwable cause) {
        super(cause);
    }

    public StatusNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}