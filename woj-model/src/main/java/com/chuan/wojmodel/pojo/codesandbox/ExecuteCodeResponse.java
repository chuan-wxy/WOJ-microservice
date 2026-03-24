package com.chuan.wojmodel.pojo.codesandbox;

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

    private String info;

    private List<String> outputList;

    private List<Long> timeList;

    private List<Long> memoryList;

    private List<Long> stackList;
}
