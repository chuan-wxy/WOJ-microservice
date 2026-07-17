package com.chuan.wojjudgeservice.codesandbox.runner;

import java.io.IOException;

public interface SandboxRunner {

    SandboxRunResult run(SandboxRunRequest request) throws IOException, InterruptedException;
}
