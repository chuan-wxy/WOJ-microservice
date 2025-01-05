package com.chuan.wojmodel.pojo.dto.activity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: chuan-wxy
 * @Date: 2024/10/2 20:33
 * @Description:
 */
@Data
public class ActivityAddDTO implements Serializable {

    /**
     * 名称
     */
    private String title;

    /**
     * 活动细则
     */
    private String description;

    /**
     * 图像
     */
    private String avatar;

    private static final long serialVersionUID = 1L;

}
