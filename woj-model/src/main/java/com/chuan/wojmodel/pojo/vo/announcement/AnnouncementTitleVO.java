package com.chuan.wojmodel.pojo.vo.announcement;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

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
    private Integer id;

    /**
     * 名称
     */
    private String title;

    /**
     * 名称
     */
    private Date createTime;

    @Serial
    private static final long serialVersionUID = 1L;
}
