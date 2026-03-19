package com.chuan.wojmodel.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author chuan_wxy
 *
 */
@Data
@AllArgsConstructor
public class JudgeCaseFileVO {
    private String fileName;
    private Long fileSize;
    private String suffix; // in 或 out
}