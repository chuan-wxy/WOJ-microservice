package com.chuan.wojcommon.common.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum JudgeInfoMessageEnum {

    ACCEPTED("成功", "ACCEPTED"),
    WRONG_ANSWER("答案错误", "WRONG_ANSWER"),
    COMPILE_ERROR("编译失败", "COMPILE_ERROR"),
    MEMORY_LIMIT_EXCEEDED("内存超出", "MEMORY_LIMIT_EXCEEDED"),
    TIME_LIMIT_EXCEECED("时间超出", "TIME_LIMIT_EXCEECED"),
    PRESENTATION_ERROR("格式错误", "PRESENTATION_ERROR"),
    OUPUT_LIMIT_EXCEECED("用户头像", "OUPUT_LIMIT_EXCEECED"),
    DANGEROUS_ERROR("危险", "Dangerous_Error"),
    RUNTIME_ERROR("超时", "Runtime_Error"),
    SYSTEM_ERROR("系统错误", "System_Error");

    private final String text;

    private final String value;

    JudgeInfoMessageEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static JudgeInfoMessageEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (JudgeInfoMessageEnum anEnum : JudgeInfoMessageEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
