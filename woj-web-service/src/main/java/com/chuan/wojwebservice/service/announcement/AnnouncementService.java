package com.chuan.wojwebservice.service.announcement;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojmodel.pojo.dto.announcement.AnnouncementAddDTO;
import com.chuan.wojmodel.pojo.entity.Announcement;
import com.chuan.wojmodel.pojo.vo.announcement.AnnouncementContentVO;
import com.chuan.wojmodel.pojo.vo.announcement.AnnouncementTitleVO;

import java.util.List;

/**
* @author lenovo
* @description 针对表【announcement】的数据库操作Service
* @createDate 2024-10-06 19:45:34
*/
public interface AnnouncementService extends IService<Announcement> {

    BaseResponse<String> addAnnouncement(AnnouncementAddDTO announcementAddDTO) throws StatusFailException;

    BaseResponse<List<AnnouncementTitleVO>> getAnnouncementList();

    BaseResponse<AnnouncementContentVO> getAnnouncement(Integer id) throws StatusFailException;
}
