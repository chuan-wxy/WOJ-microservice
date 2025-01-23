package com.chuan.wojjudgeservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse {

    private String result;

    private List<String> outputList;

    private List<Long> timeList;

    private List<Long> memoryList;

    private List<Long> stackList;

    private String errorMessage;

}
