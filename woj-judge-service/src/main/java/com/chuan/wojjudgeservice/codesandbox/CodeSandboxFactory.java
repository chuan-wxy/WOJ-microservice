package com.chuan.wojjudgeservice.codesandbox;


import com.chuan.wojjudgeservice.codesandbox.impl.CCodeSandBox;

/**
 * 代码沙箱工厂
 * 根据传来的数据，创建指定的代码沙箱
 */
public class CodeSandboxFactory {
    public static  CodeSandbox newCodeSandbox(String type) {
        switch (type) {
            case "c++":
                return new CCodeSandBox();
            default:
                return new CCodeSandBox();
        }
    }
}
