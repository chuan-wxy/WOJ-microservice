package com.chuan.wojcommon.exception;

import lombok.Data;

/**
 * 业务操作失败异常
 * 适用于：参数校验失败
 *
 * @author chuan-wxy
 */
@Data
public class StatusFailException extends Exception {
    /**
     * 业务错误码
     */
    public Integer code;

    /**
     * 无参构造器
     * 默认错误码：400，通用业务失败码
     */
    public StatusFailException() {
        super("业务操作失败");
        this.code = 400;
    }

    /**
     * 仅指定错误信息的构造器
     * 默认错误码：400
     *
     * @param message 自定义错误信息
     */
    public StatusFailException(String message) {
        super(message);
        code = 400;
    }

    /**
     * 指定错误码和错误信息的构造器
     *
     * @param code    自定义业务错误码
     * @param message 自定义错误信息
     */
    public StatusFailException(Integer code, String message) {
        super(message);
        // 兜底：避免传入null导致空指针，默认赋值400
        this.code = code != null ? code : 400;
    }

    /**
     * 指定错误信息和异常原因的构造器
     * 默认错误码：400
     *
     * @param message 自定义错误信息
     * @param cause   异常根因
     */
    public StatusFailException(String message, Throwable cause) {
        super(message, cause);
        this.code = 400;
    }

    /**
     * 仅指定异常原因的构造器
     * 默认错误码：400，默认错误信息："业务操作失败"
     *
     * @param cause 异常根因
     */
    public StatusFailException(Throwable cause) {
        super("业务操作失败", cause);
        this.code = 400;
    }

    /**
     * 全参数构造器（包含抑制异常、栈轨迹可写性）
     * 适用于特殊场景（如：需要控制异常栈轨迹的生成）
     *
     * @param message            自定义错误信息
     * @param cause              异常根因
     * @param enableSuppression  是否允许抑制异常
     * @param writableStackTrace 是否生成可写的栈轨迹
     */
    public StatusFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = 400;
    }

    /**
     * 全参数构造器（包含错误码）
     * 补充原代码缺失的"错误码+根因"场景
     *
     * @param code    自定义业务错误码
     * @param message 自定义错误信息
     * @param cause   异常根因
     */
    public StatusFailException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code != null ? code : 400;
    }
}