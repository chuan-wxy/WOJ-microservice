package com.chuan.wojcommon.common.enums;

/**
 * 邮件常量枚举类
 *
 * @Author: chuan-wxy
 * @Date: 2024/8/16 12:45
 * @Description: 邮件任务的一些常量
 */
public enum EmailEnum {

    OJ_URL("OJ_UR"),
    OJ_NAME("OJ_NAME"),
    OJ_SHORT_NAME("OJ_SHORT_NAME"),
    EMAIL_FROM("EMAIL_FROM"),
    EMAIL_BACKGROUND_IMG("EMAIL_BACKGROUND_IMG"),
    REGISTER_KEY_PREFIX("register-user:"),
    LOGOUT_KEY_PREFIX("logout-user:"),
    CHANGE_EMAIL_KEY_PREFIX("change-email:"),
    RESET_PASSWORD_KEY_PREFIX("reset-password:"),
    RESET_EMAIL_LOCK("reset-email-lock:"),
    REGISTER_EMAIL_LOCK("register-email-lock:"),
    LOGOUT_EMAIL_LOCK("logout-email-lock:"),
    CHANGE_EMAIL_LOCK("change-email-lock:");

    private final String value;

    EmailEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
