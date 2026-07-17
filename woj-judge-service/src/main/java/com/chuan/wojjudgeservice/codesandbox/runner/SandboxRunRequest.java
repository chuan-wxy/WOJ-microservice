package com.chuan.wojjudgeservice.codesandbox.runner;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class SandboxRunRequest {

    private List<String> command;

    private List<String> isolateCommand;

    private String workDir;

    private String stdinPath;

    private String stdoutPath;

    private String stderrPath;

    private String metaFileName;

    private long timeLimitMillis;

    private long wallTimeLimitMillis;

    private Long memoryLimitKb;

    @Builder.Default
    private int processLimit = 20;

    private boolean redirectErrorStream;

    private boolean compilerEnv;

    private Map<String, String> env;
}
