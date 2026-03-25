package com.chuan.wojmodel.pojo.dto.problem;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ProblemTagDTO
 *
 * @Author: chuan-wxy
 * @Date: 2024/8/28 15:17
 * @Description: 与ProblemTitleDTO封装成ProblemTitleVO，用于前端请求题目及其标签
 */
@Data
public class ProblemTagDTO {
    private Long pid;

    private String problemId;

    private String problemName;

}
