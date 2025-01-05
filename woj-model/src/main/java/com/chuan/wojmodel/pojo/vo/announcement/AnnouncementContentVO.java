package com.chuan.wojmodel.pojo.vo.announcement;

import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

/**
 * @Author: chuan-wxy
 * @Date: 2024/10/2 21:06
 * @Description:
 */
@Data
public class AnnouncementContentVO implements Serializable {
    /**
     *
     */
    private Long id;

    /**
     * 名称
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    private Date createTime;

    private static final long serialVersionUID = 1L;
}
