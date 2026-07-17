package com.chuan.wojmodel.pojo;

import lombok.Data;

@Data
public class ExecuteMessage {
    // 标志位，0代表正常
    private Integer exitValue;

    private String result;

    private String info;

    private Long time;

    private Long memory;
}
