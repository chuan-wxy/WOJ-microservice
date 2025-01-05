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
    private List<String> outputList;
    /**
     * 接口信息
     */
    private String message;
    /**
     * 状态码
     * 0-成功
     * 1-编译失败 Compile Error
     */
    private Integer status;
    /**
     * 用时列表
     * 存放沙箱每一样例执行的时间
     */
    private List<Long> timeList;
}
