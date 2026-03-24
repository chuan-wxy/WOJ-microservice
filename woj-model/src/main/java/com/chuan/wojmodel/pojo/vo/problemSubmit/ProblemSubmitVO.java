package com.chuan.wojmodel.pojo.vo.problemSubmit;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: chuan-wxy
 * @Date: 2024/9/12 12:57
 * @Description:
 */
@Data
public class ProblemSubmitVO  implements Serializable {

    private Long id;

    private String judgeResult;

    private String judgeInfo;

    private List<Long> timeLit;

    private List<Long> memoryLit;

    private List<Long> stackLit;

    private Long pid;

    private String uid;

    @Serial
    private static final long serialVersionUID = 1L;
}
