package com.chuan.wojmodel.pojo.dto.course;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: chuan-wxy
 * @Date: 2024/9/25 17:06
 * @Description:
 */
@Data
public class CourseAddDTO implements Serializable{

    /**
     * 名称
     */
    private String name;

    /**
     * 父节点id
     */
    private Long pid;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 描述
     */
    private String description;

    /**
     * 图像
     */
    private String avatar;

    private static final long serialVersionUID = 1L;

}

