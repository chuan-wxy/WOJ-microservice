package com.chuan.wojmodel.pojo.dto.problem;

import lombok.Data;

import java.io.Serializable;

/**
 * ProblemTagDTO
 *
 * @Author: chuan-wxy
 * @Date: 2024/8/28 15:17
 * @Description: 与ProblemTitleDTO封装成ProblemTitleVO，用于前端请求题目及其标签
 */
@Data
public class ProblemTagDTO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 标签名字
     */
    private String name;

    private static final long serialVersionUID = 1L;
}
