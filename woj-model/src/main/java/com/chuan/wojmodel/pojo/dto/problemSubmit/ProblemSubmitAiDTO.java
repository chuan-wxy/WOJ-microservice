package com.chuan.wojmodel.pojo.dto.problemSubmit;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author chuan_wxy
 *
 */
@Data
public class ProblemSubmitAiDTO implements Serializable {
    /**
     * 题目表 `problem` 的主键 pid，用于让 AI 在 function/tool 调用时查询本地题目标签。
     */
    private Long pid;

    @NotBlank(message = "代码语言不能为空")
    private String language;

    @NotBlank(message = "代码不能为空")
    private String code;

    @NotBlank(message = "判题结果不能为空")
    private String judgeResult;

    private String judgeInfo;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "题目描述不能为空")
    private String description;

    private String question;

    @Serial
    private static final long serialVersionUID = 1L;
}
