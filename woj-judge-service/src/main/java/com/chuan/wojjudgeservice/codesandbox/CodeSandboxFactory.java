package com.chuan.wojjudgeservice.codesandbox;

import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojjudgeservice.codesandbox.impl.CCodeSandBox;
import org.springframework.stereotype.Component;

/**
 * 代码沙箱工厂
 * 根据传来的代码语言，创建指定的代码沙箱
 */
@Component
public class CodeSandboxFactory {

    private final CCodeSandBox cCodeSandBox;

    public CodeSandboxFactory(CCodeSandBox cCodeSandBox) {
        this.cCodeSandBox = cCodeSandBox;
    }

    /**
     * 根据代码语言，返回对应语言的 CodeSandbox
     *
     * @param language
     * @return CodeSandbox
     */
    public CodeSandbox getSandbox(String language) throws StatusFailException {
        String normalizedLanguage = normalizeLanguage(language);

        if ("c++".equals(normalizedLanguage)) {
            return cCodeSandBox;
        }

        throw new StatusFailException("不支持该编程语言：" + language);
    }

    private String normalizeLanguage(String language) throws StatusFailException {
        if (language == null || language.isBlank()) {
            throw new StatusFailException("编程语言不能为空");
        }
        return language.trim().toLowerCase();
    }
}
