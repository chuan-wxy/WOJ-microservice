package com.chuan.wojjudgeservice.codesandbox;

import com.chuan.wojcommon.common.enums.ProblemSubmitLanguageEnum;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojjudgeservice.codesandbox.impl.CCodeSandBox;
import com.chuan.wojjudgeservice.codesandbox.impl.JavaCodeSandBox;
import org.springframework.stereotype.Component;

/**
 * Code sandbox factory.
 */
@Component
public class CodeSandboxFactory {

    private final CCodeSandBox cCodeSandBox;

    private final JavaCodeSandBox javaCodeSandBox;

    public CodeSandboxFactory(CCodeSandBox cCodeSandBox, JavaCodeSandBox javaCodeSandBox) {
        this.cCodeSandBox = cCodeSandBox;
        this.javaCodeSandBox = javaCodeSandBox;
    }

    public CodeSandbox getSandbox(String language) throws StatusFailException {
        ProblemSubmitLanguageEnum languageEnum = ProblemSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new StatusFailException("不支持该编程语言：" + language);
        }

        if (ProblemSubmitLanguageEnum.CPP.equals(languageEnum)) {
            return cCodeSandBox;
        }

        if (ProblemSubmitLanguageEnum.JAVA.equals(languageEnum)) {
            return javaCodeSandBox;
        }

        throw new StatusFailException("不支持该编程语言：" + language);
    }
}
