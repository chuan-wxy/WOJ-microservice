package com.chuan.wojmodel.pojo.dto.problem;

import lombok.Data;

import java.io.Serializable;

/**
 * ProblemTitleDTO
 *
 * @Author: chuan-wxy
 * @Date: 2024/8/28 15:17
 * @Description: 与ProblemTagDTO封装成ProblemTitleVO，用于前端请求题目及其标签
 */
@Data
public class ProblemTitleDTO implements Serializable {
    /**
     * ID
     */
    private Long id;

    /**
     * 题目
     */
    private String title;

    /**
     * 题目来源
     */
    private String source;

    /**
     * 题目难度,0简单，1中等，2困难
     */
    private Integer difficulty;

    private static final long serialVersionUID = 1L;
}
