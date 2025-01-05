package com.chuan.wojmodel.pojo.vo.problemSubmit;

import com.chuan.wojmodel.pojo.dto.problemSubmit.JudgeInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: chuan-wxy
 * @Date: 2024/9/12 12:57
 * @Description:
 */
@Data
public class ProblemSubmitVO  implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 代码语言
     */
    private String language;

    /**
     * 提交代码
     */
    private String code;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;
    /**
     * 判题状态（0 - 待判题、1 - 判题中、2 - 判题结束）
     */
    private Integer state;

    /**
     * 题目id
     */
    private Long pid;

    /**
     * 用户id
     */
    private String uid;

    private static final long serialVersionUID = 1L;
}
