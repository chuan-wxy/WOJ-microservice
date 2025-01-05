package com.chuan.wojmodel.pojo.vo.activity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: chuan-wxy
 * @Date: 2024/10/2 21:06
 * @Description:
 */
@Data
public class ActivityTitleVO  implements Serializable {
    /**
     *
     */
    private Long id;

    /**
     * 名称
     */
    private String title;

    /**
     * 名称
     */
    private String avatar;

    private static final long serialVersionUID = 1L;
}
