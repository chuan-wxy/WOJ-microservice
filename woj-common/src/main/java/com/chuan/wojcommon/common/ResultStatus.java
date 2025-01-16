package com.chuan.wojcommon.common;

/**
 * 状态码枚举类
 *
 * @author chuan-wxy
 * @date 2024/08/14 04:00:47
 */

public enum ResultStatus {

    OK(200,"OK"),
    CREATED(201, "Created"),
    ACCEPTED(202,  "Accepted"),
    NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
    NO_CONTENT(204,  "No Content"),
    RESET_CONTENT(205,  "Reset Content"),
    PARTIAL_CONTENT(206,  "Partial Content"),
    MULTI_STATUS(207,  "Multi-Status"),
    ALREADY_REPORTED(208,  "Already Reported"),
    IM_USED(226, "IM Used"),
    MULTIPLE_CHOICES(300,  "Multiple Choices"),
    MOVED_PERMANENTLY(301,  "Moved Permanently"),
    FOUND(302,  "Found"),
    TEMPORARY_REDIRECT(307,  "Temporary Redirect"),
    PERMANENT_REDIRECT(308,  "Permanent Redirect"),
    FAIL(400,  "FAIL"),
    UNAUTHORIZED(401,  "Unauthorized"),
    PAYMENT_REQUIRED(402,  "Payment Required"),
    FORBIDDEN(403,  "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407,  "Proxy Authentication Required"),
    SYSTEM_ERROR(500,"系统错误");

    private int code;

    private String message;

    ResultStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
