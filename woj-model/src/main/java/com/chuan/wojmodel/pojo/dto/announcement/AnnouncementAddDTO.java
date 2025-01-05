package com.chuan.wojmodel.pojo.dto.announcement;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: chuan-wxy
 * @Date: 2024/10/2 20:33
 * @Description:
 */
@Data
public class AnnouncementAddDTO implements Serializable {

    /**
     * 名称
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    private static final long serialVersionUID = 1L;

}
