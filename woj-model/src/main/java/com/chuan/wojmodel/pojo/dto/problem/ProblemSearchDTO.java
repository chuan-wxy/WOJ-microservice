package com.chuan.wojmodel.pojo.dto.problem;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author chuan_wxy
 *
 */
@Data
public class ProblemSearchDTO  implements Serializable {

    private Long id;

    private String problemId;

    private String title;

    private String author;

    private String source;

    private Integer difficulty;

    @Serial
    private static final long serialVersionUID = 1L;
}
