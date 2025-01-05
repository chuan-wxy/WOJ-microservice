package com.chuan.wojmodel.pojo.dto.problem;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: chuan-wxy
 * @Date: 2024/8/27 15:48
 * @Description:
 */
@Data
public class TagAddDTO implements Serializable {

    /**
     * 标签名字
     */
    private String name;

    private static final long serialVersionUID = 1L;
}
