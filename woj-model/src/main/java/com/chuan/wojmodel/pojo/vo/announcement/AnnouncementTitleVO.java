package com.chuan.wojmodel.pojo.vo.announcement;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: chuan-wxy
 * @Date: 2024/10/2 21:06
 * @Description:
 */
@Data
public class AnnouncementTitleVO implements Serializable {
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
    private String createTime;

    private static final long serialVersionUID = 1L;
}
