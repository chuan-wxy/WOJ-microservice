package com.chuan.wojmodel.pojo.dto.problem;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: chuan-wxy
 * @Date: 2024/8/26 16:35
 * @Description:
 */
@Data
public class ProblemAddDTO implements Serializable {

    private String problemId;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String author;

    private List<String> tagList;

    private Integer timeLimit;

    private Integer memoryLimit;

    private Integer stackLimit;

    @NotBlank(message="题目描述不能为空")
    private String description;

    private String source;

    @NotNull
    private Integer difficulty;

    private Integer auth;

    private String judgeMode;

    private String spjCode;

    private String spjLanguage;

    @Serial
    private static final long serialVersionUID = 1L;
}
