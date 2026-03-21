package com.chuan.wojcommon.common.enums;

import lombok.Getter;

/**
 * @author chuan_wxy
 *
 */
@Getter
public enum ProblemSubmitStateEnum {

    /**
     * 等待评测
     */
    PENDING(0, "等待评测"),

    /**
     * 正在评测中
     */
    JUDGING(1, "正在评测"),

    /**
     * 评测完成
     */
    ACCEPTED(2, "评测完成");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 状态描述
     */
    private final String desc;

    ProblemSubmitStateEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     * @param code 状态码
     * @return 对应的枚举
     * @throws IllegalArgumentException 如果状态码不存在
     */
    public static ProblemSubmitStateEnum fromCode(int code) {
        for (ProblemSubmitStateEnum state : values()) {
            if (state.getCode() == code) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid ProblemState code: " + code);
    }
}