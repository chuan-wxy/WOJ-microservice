package com.chuan.wojmodel.pojo.vo.problem;
import java.util.ArrayList;
import java.util.Date;

import cn.hutool.json.JSONUtil;
import com.chuan.wojmodel.pojo.entity.Problem;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: chuan-wxy
 * @Date: 2024/8/28 14:27
 * @Description:
 */
@Data
public class ProblemTitleVO implements Serializable {
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
     * 标签
     */
    private List<String> tagList;

    /**
     * 题目来源
     */
    private String source;

    /**
     * 题目难度,0简单，1中等，2困难
     */
    private Integer difficulty;

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 实体类转封装类
     * tagList 需要手动赋值
     * @param problem
     * @return ProblemTitleVO
     */
    public static ProblemTitleVO objToVo(Problem problem) {
        if (problem == null) return null;

        ProblemTitleVO problemTitleVO = new ProblemTitleVO();
        problemTitleVO.setProblemId(problem.getProblemId());
        problemTitleVO.setTitle(problem.getTitle());
        problemTitleVO.setId(problem.getId());
        problemTitleVO.setSource(problem.getSource());
        problemTitleVO.setDifficulty(problem.getDifficulty());

        return problemTitleVO;
    }
}
