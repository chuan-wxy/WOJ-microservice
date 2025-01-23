package com.chuan.wojmodel.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @TableName problem_submit
 */
@TableName(value ="problem_submit")
@Data
public class ProblemSubmit implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 题目id
     */
    private Long pid;

    /**
     * 用户id
     */
    private String uid;

    /**
     * 代码语言
     */
    private String language;

    /**
     * 提交代码
     */
    private String code;

    /**
     * 判题结果
     */
    private String judgeResult;

    /**
     * 用时列表
     */
    private String timeList;

    /**
     * 内存列表
     */
    private String memoryList;

    /**
     * 栈内存列表
     */
    private String stackList;

    /**
     *
     */
    private Date createtime;

    /**
     *
     */
    private Date updatetime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}