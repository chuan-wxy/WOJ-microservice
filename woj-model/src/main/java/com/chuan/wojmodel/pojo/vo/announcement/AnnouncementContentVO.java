package com.chuan.wojmodel.pojo.vo.announcement;

import com.chuan.wojmodel.pojo.entity.Announcement;
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
public class AnnouncementContentVO implements Serializable {
    /**
     *
     */
    private Integer id;

    /**
     * 名称
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    private Date createTime;

    @Serial
    private static final long serialVersionUID = 1L;

    public static AnnouncementContentVO objToVo(Announcement announcement){
        AnnouncementContentVO obj = new AnnouncementContentVO();
        obj.setId(announcement.getId());
        obj.setContent(announcement.getContent());
        obj.setTitle(announcement.getTitle());
        obj.setCreateTime(announcement.getCreateTime());
        return obj;
    }

}
