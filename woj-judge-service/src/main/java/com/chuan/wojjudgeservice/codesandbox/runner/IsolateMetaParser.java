package com.chuan.wojjudgeservice.codesandbox.runner;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class IsolateMetaParser {

    public Map<String, String> parse(Path metaPath) throws IOException {
        Map<String, String> meta = new HashMap<>();
        if (!Files.exists(metaPath)) {
            return meta;
        }
        List<String> lines = Files.readAllLines(metaPath);
        for (String line : lines) {
            int separator = line.indexOf(':');
            if (separator <= 0) {
                continue;
            }
            String key = line.substring(0, separator).trim();
            String value = line.substring(separator + 1).trim();
            meta.put(key, value);
        }
        return meta;
    }
}
