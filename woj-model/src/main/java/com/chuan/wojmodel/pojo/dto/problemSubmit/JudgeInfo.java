package com.chuan.wojmodel.pojo.dto.problemSubmit;

import lombok.Data;

import java.util.List;

/**
 * 判题信息DTO
 *
 * @Author: chuan-wxy
 * @Date: 2024/9/12 13:17
 * @Description:
 */
@Data
public class JudgeInfo {

    private String message;
    /**
     * 时间
     */
    private List<Long> timeList;

    /**
     * 内存kb
     */
    private List<Long> memoryList;
}
