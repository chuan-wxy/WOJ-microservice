package com.chuan.wojmodel.pojo.vo.problem;
import java.util.ArrayList;

import com.chuan.wojmodel.pojo.entity.Problem;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: chuan-wxy
 * @Date: 2024/8/26 16:30
 * @Description:
 */
@Data
public class ProblemVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 问题的自定义ID
     */
    private String problemId;

    /**
     * 题目
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 标签
     */
    private List<String> tagList;

    /**
     * 单位ms
     */
    private Integer timeLimit;

    /**
     * 单位kb
     */
    private Integer memoryLimit;

    /**
     * 单位mb
     */
    private Integer stackLimit;

    /**
     * 描述
     */
    private String description;

    /**
     * 题目来源
     */
    private String source;

    /**
     * 题目难度,0简单，1中等，2困难
     */
    private Integer difficulty;

    /**
     * 默认为1公开，2为私有，3为比赛题目
     */
    private Integer auth;

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 实体类转封装类
     * tagList 需要手动赋值
     * @param problem
     * @return ProblemVO
     */
    public static ProblemVO objToVo(Problem problem) {
        if (problem == null) return null;

        ProblemVO problemVO = new ProblemVO();
        problemVO.setId(problem.getId());
        problemVO.setProblemId(problem.getProblemId());
        problemVO.setTitle(problem.getTitle());
        problemVO.setAuthor(problem.getAuthor());
        problemVO.setTimeLimit(problem.getTimeLimit());
        problemVO.setMemoryLimit(problem.getMemoryLimit());
        problemVO.setStackLimit(problem.getStackLimit());
        problemVO.setDescription(problem.getDescription());
        problemVO.setSource(problem.getSource());
        problemVO.setDifficulty(problem.getDifficulty());
        problemVO.setAuth(problem.getAuth());

        return problemVO;
    }
}
