package com.chuan.wojmodel.pojo.dto.problem;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author chuan_wxy
 *
 */
@Data
public class ProblemContentDTO  implements Serializable {
    private Long id;

    private String title;

    private String description;

    @Serial
    private static final long serialVersionUID = 1L;
}
