package com.chuan.wojjudgeservice.codesandbox.runner;

import org.springframework.stereotype.Component;

@Component
public class SandboxRunnerFactory {

    private final LocalSandboxRunner localSandboxRunner;

    private final IsolateSandboxRunner isolateSandboxRunner;

    private final SandboxRunnerProperties properties;

    public SandboxRunnerFactory(LocalSandboxRunner localSandboxRunner,
                                IsolateSandboxRunner isolateSandboxRunner,
                                SandboxRunnerProperties properties) {
        this.localSandboxRunner = localSandboxRunner;
        this.isolateSandboxRunner = isolateSandboxRunner;
        this.properties = properties;
    }

    public SandboxRunner getRunner() {
        if ("isolate".equalsIgnoreCase(properties.getRunner())) {
            return isolateSandboxRunner;
        }
        return localSandboxRunner;
    }
}
