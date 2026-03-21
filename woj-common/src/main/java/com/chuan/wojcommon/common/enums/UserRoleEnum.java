package com.chuan.wojcommon.common.enums;

import lombok.Data;

import java.io.PrintStream;

/**
 * 用户角色枚举类
 *
 * @Author: chuan-wxy
 * @Date: 2024/8/20 15:23
 * @Description:
 */

public enum UserRoleEnum {
    ROOR("root", 1),
    AMDIN("admin", 2),
    DEFAULT_USER("default_user", 3);

    private String userRole;

    private int value;

    UserRoleEnum(String userRole, int value) {
        this.userRole = userRole;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getText() { return userRole; }

}
