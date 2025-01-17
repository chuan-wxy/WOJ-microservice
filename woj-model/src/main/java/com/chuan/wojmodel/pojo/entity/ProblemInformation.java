package com.chuan.wojmodel.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName problem_information
 */
@TableName(value ="problem_information")
@Data
public class ProblemInformation implements Serializable {
    /**
     * 题目id
     */
    @TableId
    private Long pid;

    /**
     * 总提交数
     */
    private Integer subnum;

    /**
     * AC
     */
    private Integer acnum;

    /**
     * 编译错误
     */
    private Integer cenum;

    /**
     * 答案错误
     */
    private Integer wanum;

    /**
     * 时间超限
     */
    private Integer tlenum;

    /**
     * 内存超限
     */
    private Integer mlenum;

    /**
     * 运行错误
     */
    private Integer renum;

    /**
     * 段错误
     */
    private Integer sfnum;

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