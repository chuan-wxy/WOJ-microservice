package com.chuan.wojmodel.pojo;

import lombok.Data;

@Data
public class ExecuteMessage {
    private Integer exitValue;

    private String result;

    private String info;

    private Long time;

    private Long memory;
}
