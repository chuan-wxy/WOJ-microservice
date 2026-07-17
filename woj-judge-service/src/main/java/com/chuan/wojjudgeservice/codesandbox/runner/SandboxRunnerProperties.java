package com.chuan.wojjudgeservice.codesandbox.runner;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sandbox")
public class SandboxRunnerProperties {

    /**
     * local or isolate.
     */
    private String runner = "local";

    private Isolate isolate = new Isolate();

    @Data
    public static class Isolate {

        private String executable = "isolate";

        private int boxId = 0;

        private boolean cg = true;

        private String pathEnv = "/usr/bin:/bin";

        private long defaultMemoryLimitKb = 262144L;
    }
}
