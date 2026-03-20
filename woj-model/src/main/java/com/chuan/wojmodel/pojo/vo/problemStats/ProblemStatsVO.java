package com.chuan.wojmodel.pojo.vo.problemStats;

import lombok.Data;

import java.util.Date;

/**
 * @author chuan_wxy
 *
 */
@Data
public class ProblemStatsVO {

    private Long pid;

    private CharDataVO charDataVO;

    private Integer totalSubmissions;

    private Integer totalAttemptedUsers;

    private Integer solvedUsers;

    private Date lastSubmittedAt;
}