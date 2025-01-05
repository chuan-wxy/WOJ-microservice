package com.chuan.wojmodel.pojo.vo.problem;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: chuan-wxy
 * @Date: 2024/9/9 17:05
 * @Description:
 */
@Data
public class TagVO implements Serializable {

    /**
     * 标签名
     */
    private String name;

    private static final long serialVersionUID = 1L;
}
