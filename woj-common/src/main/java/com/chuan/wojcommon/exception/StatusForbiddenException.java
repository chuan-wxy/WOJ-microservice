package com.chuan.wojcommon.exception;

import lombok.Data;

/**
 * 禁止访问异常
 * 适用于：资源层面禁止访问的场景（非权限不足），如IP被拉黑、接口未开放、未授权访问私密资源、跨域禁止等
 *
 * @author chuan-wxy
 */
@Data
public class StatusForbiddenException extends Exception {
    /**
     * 业务错误码
     */
    private Integer code;

    /**
     * 无参构造器
     * 默认错误码：403，默认错误信息："禁止访问该资源"
     */
    public StatusForbiddenException() {
        super("禁止访问该资源");
        this.code = 403;
    }

    /**
     * 仅指定错误信息的构造器
     * 默认错误码：403
     *
     * @param message 自定义错误信息（如："您的IP已被限制访问该接口"）
     */
    public StatusForbiddenException(String message) {
        super(message);
        this.code = 403;
    }

    /**
     * 指定错误码和错误信息的构造器
     *
     * @param code    自定义业务错误码
     * @param message 自定义错误信息
     */
    public StatusForbiddenException(Integer code, String message) {
        super(message);
        // 兜底：避免传入null导致空指针，默认赋值403
        this.code = code != null ? code : 403;
    }

    /**
     * 指定错误信息和异常原因的构造器
     * 默认错误码：403
     *
     * @param message 自定义错误信息
     * @param cause   异常根因
     */
    public StatusForbiddenException(String message, Throwable cause) {
        super(message, cause);
        this.code = 403;
    }

    /**
     * 仅指定异常原因的构造器
     * 默认错误码：403，默认错误信息："禁止访问该资源"
     *
     * @param cause 异常根因
     */
    public StatusForbiddenException(Throwable cause) {
        super("禁止访问该资源", cause);
        this.code = 403;
    }

    /**
     * 全参数构造器（包含抑制异常、栈轨迹可写性）
     * 适用于特殊场景（如：需要控制异常栈轨迹生成、禁止异常抑制）
     *
     * @param message            自定义错误信息
     * @param cause              异常根因
     * @param enableSuppression  是否允许抑制异常
     * @param writableStackTrace 是否生成可写的栈轨迹
     */
    public StatusForbiddenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = 403;
    }

    /**
     * 扩展构造器：指定错误码、错误信息和异常根因
     * 适用于：需要自定义错误码且记录异常根因的场景（如：4032-接口未开放，且根因是配置读取失败）
     *
     * @param code    自定义业务错误码
     * @param message 自定义错误信息
     * @param cause   异常根因
     */
    public StatusForbiddenException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code != null ? code : 403;
    }
}