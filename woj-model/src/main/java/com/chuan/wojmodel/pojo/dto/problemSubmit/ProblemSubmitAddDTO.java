package com.chuan.wojmodel.pojo.dto.problemSubmit;

import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: chuan-wxy
 * @Date: 2024/9/12 12:43
 * @Description:
 */
@Data
public class ProblemSubmitAddDTO implements Serializable {
    /** 代码语言 */
    @NotBlank(message = "语言不能为空")
    private String language;

    /** 提交代码 */
    @NotBlank(message = "代码不能为空")
    private String code;

    /** 题目 Id */
    @NotBlank(message = "题目 Id 不能为空")
    private String pid;

    @Serial
    private static final long serialVersionUID = 1L;
}
