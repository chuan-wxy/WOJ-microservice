package com.chuan.wojwebservice.service.announcement.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.utils.ResultUtils;
import com.chuan.wojmodel.pojo.dto.announcement.AnnouncementAddDTO;
import com.chuan.wojmodel.pojo.entity.Announcement;
import com.chuan.wojmodel.pojo.vo.announcement.AnnouncementContentVO;
import com.chuan.wojmodel.pojo.vo.announcement.AnnouncementTitleVO;
import com.chuan.wojwebservice.manager.ActivityManager;
import com.chuan.wojwebservice.mapper.AnnouncementMapper;
import com.chuan.wojwebservice.service.announcement.AnnouncementService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author chuan-wxy
* @description 针对表【announcement】的数据库操作Service实现
* @createDate 2024-10-06 19:45:34
*/
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement>
    implements AnnouncementService {

    @Autowired
    private ActivityManager activityManager;

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public BaseResponse<String> addAnnouncement(AnnouncementAddDTO announcementAddDTO) throws StatusFailException {
        activityManager.announcementAddvalidate(announcementAddDTO);

        Announcement announcement = new Announcement();

        BeanUtils.copyProperties(announcementAddDTO,announcement);

        boolean save = this.save(announcement);

        if(save) {
            return ResultUtils.success("保存成功");
        } else {
            log.error("AnnouncementServiceImpl---->addAnnouncement---保存公告失败");
            return ResultUtils.error("保存失败");
        }
    }


    @Override
    public BaseResponse<List<AnnouncementTitleVO>> getAnnouncementList() {
        List<AnnouncementTitleVO> announcementTitleVOS = announcementMapper.selectAnnouncementTitleList();
        for(int i = 0;i<announcementTitleVOS.size();i++) {
            announcementTitleVOS.get(i).setCreateTime(announcementTitleVOS.get(i).getCreateTime().substring(0, 10));
        }
        return ResultUtils.success(announcementTitleVOS);
    }

    @Override
    public BaseResponse<AnnouncementContentVO> getAnnouncement(Integer id) throws StatusFailException {
        if(id == null) {
            throw new StatusFailException("id为空");
        }
        if(id <= 0) {
            throw new StatusFailException("参数错误");
        }
        return ResultUtils.success(announcementMapper.selectAnnouncementContentVO(id));
    }
}




