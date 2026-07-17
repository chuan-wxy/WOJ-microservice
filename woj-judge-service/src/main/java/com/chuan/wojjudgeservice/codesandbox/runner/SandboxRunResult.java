package com.chuan.wojjudgeservice.codesandbox.runner;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class SandboxRunResult {

    private int exitCode;

    private boolean timedOut;

    private String output;

    private Long timeMillis;

    private Long memoryKb;

    private Map<String, String> meta;
}
