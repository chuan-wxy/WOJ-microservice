package com.chuan.wojmodel.pojo.vo.problemSubmit;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
    private String result;
    private List<Long> timeLit;
    private List<Long> memoryLit;
    private List<Long> stackLit;

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
