package com.chuan.wojmodel.pojo.dto.problem;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ProblemUpdateDTO
 *
 * @Author: chuan-wxy
 * @Date: 2024/9/24 12:52
 * @Description:
 */
@Data
public class ProblemUpdateDTO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 问题的自定义ID
     */
    private String problemId;

    /**
     * 题目
     */
    private String title;

    /**
     * 标签
     */
    private List<String> tagList;

    /**
     * 单位ms
     */
    private Integer timeLimit;

    /**
     * 单位kb
     */
    private Integer memoryLimit;

    /**
     * 单位mb
     */
    private Integer stackLimit;

    /**
     * 描述
     */
    private String description;

    /**
     * 输入描述
     */
    private String input;

    /**
     * 输出描述
     */
    private String output;

    /**
     * 题目来源
     */
    private String source;

    /**
     * 题目难度,0简单，1中等，2困难
     */
    private Integer difficulty;

    /**
     * 默认为1公开，2为私有，3为比赛题目
     */
    private Integer auth;

    private static final long serialVersionUID = 1L;
}
