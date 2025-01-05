package com.chuan.wojmodel.pojo.dto.problemSubmit;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: chuan-wxy
 * @Date: 2024/9/12 12:43
 * @Description:
 */
@Data
public class ProblemSubmitAddDTO implements Serializable {
    /**
     * 代码语言
     */
    private String language;

    /**
     * 提交代码
     */
    private String code;

    /**
     * 题目id
     */
    private Long pid;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
