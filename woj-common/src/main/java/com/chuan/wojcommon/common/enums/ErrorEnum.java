package com.chuan.wojcommon.common.enums;

/**
 * 错误类
 *
 * @Author: chuan-wxy
 * @Date: 2024/8/16 19:15
 * @Description:
 */
public enum ErrorEnum {
    SUCCESS(200,"成功"),
    UNAUTHORIZED(401,"未登录或登录过期，请重新登录");

    private final int code;
    private final String text;

    ErrorEnum(int code,String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
