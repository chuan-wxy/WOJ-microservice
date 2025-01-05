package com.chuan.wojwebservice.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojmodel.pojo.dto.activity.ActivityAddDTO;
import com.chuan.wojmodel.pojo.dto.announcement.AnnouncementAddDTO;
import com.chuan.wojmodel.pojo.entity.Activity;
import com.chuan.wojmodel.pojo.entity.Announcement;
import com.chuan.wojwebservice.mapper.ActivityMapper;
import com.chuan.wojwebservice.mapper.AnnouncementMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: chuan-wxy
 * @Date: 2024/10/2 20:46
 * @Description:
 */
@Slf4j
@Component
public class ActivityManager {

    @Autowired
    ActivityMapper activityMapper;

    @Autowired
    AnnouncementMapper announcementMapper;
    public void activityAddvalidate(ActivityAddDTO activityAddDTO) throws StatusFailException {
        String title = activityAddDTO.getTitle();

        if(title == null || title.isBlank()) {
            throw new StatusFailException("标题不能为空");
        }

        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title",title);

        Activity activity = activityMapper.selectOne(queryWrapper);

        if(activity != null) {
            throw new StatusFailException("标题重复");
        }

    }

    public void announcementAddvalidate(AnnouncementAddDTO announcementAddDTO) throws StatusFailException {
        String title = announcementAddDTO.getTitle();

        if(title == null || title.isBlank()) {
            throw new StatusFailException("标题不能为空");
        }

        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title",title);

        Announcement announcement = announcementMapper.selectOne(queryWrapper);

        if(announcement != null) {
            throw new StatusFailException("标题重复");
        }

    }
}
