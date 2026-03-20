package com.chuan.wojmodel.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName problem_stats
 */
@TableName(value ="problem_stats")
@Data
public class ProblemStats {
    /**
     * 题目唯一标识
     */
    @TableId
    private Long pid;

    /**
     * AC - 通过次数
     */
    private Integer acCount;

    /**
     * WA - 答案错误次数
     */
    private Integer waCount;

    /**
     * TLE - 运行超时次数
     */
    private Integer tleCount;

    /**
     * MLE - 内存超限次数
     */
    private Integer mleCount;

    /**
     * RE - 运行错误次数
     */
    private Integer reCount;

    /**
     * CE - 编译错误次数
     */
    private Integer ceCount;

    /**
     * SE - 系统错误次数
     */
    private Integer seCount;

    /**
     * 总提交次数
     */
    private Integer totalSubmissions;

    /**
     * 尝试过的总人数
     */
    private Integer totalAttemptedUsers;

    /**
     * 解决该题的总人数
     */
    private Integer solvedUsers;

    /**
     * 最后一次提交时间
     */
    private Date lastSubmittedAt;
}