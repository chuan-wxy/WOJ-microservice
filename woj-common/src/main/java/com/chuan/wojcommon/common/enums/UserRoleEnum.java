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
    ROOR("root", 0),
    AMDIN("admin", 1),

    DEFAULT_USER("default_user", 2),

    MUTE_USER("mute_user", 3),

    NO_DISCUSS_USER("no_discuss_user", 4),


    NO_SUBMIT_MUTE_USER("no_submit_mute_user", 5),
    NO_SUBMIT_NO_DISCUSS_USER("no_submit_no_discuss_user", 6),
    NO_SUBMIT_USER("no_submit_user", 7),
    PROBLEM_ADMIN("problem_admin", 8);

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
