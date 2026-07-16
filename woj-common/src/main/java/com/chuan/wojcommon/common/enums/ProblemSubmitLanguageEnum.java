package com.chuan.wojcommon.common.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Problem submit language enum.
 */
public enum ProblemSubmitLanguageEnum {

    JAVA("Java", "java", Set.of("java", "java17")),
    CPP("C++", "c++", Set.of("c++", "cpp", "cpp17"));

    private final String text;

    private final String value;

    private final Set<String> aliases;

    ProblemSubmitLanguageEnum(String text, String value, Set<String> aliases) {
        this.text = text;
        this.value = value;
        this.aliases = aliases;
    }

    public static List<String> getValues() {
        return Arrays.stream(values()).map(ProblemSubmitLanguageEnum::getValue).collect(Collectors.toList());
    }

    public static ProblemSubmitLanguageEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        String normalizedValue = normalize(value);
        for (ProblemSubmitLanguageEnum languageEnum : ProblemSubmitLanguageEnum.values()) {
            if (languageEnum.value.equals(normalizedValue) || languageEnum.aliases.contains(normalizedValue)) {
                return languageEnum;
            }
        }
        return null;
    }

    public static String normalizeValue(String value) {
        ProblemSubmitLanguageEnum languageEnum = getEnumByValue(value);
        return languageEnum == null ? null : languageEnum.getValue();
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
