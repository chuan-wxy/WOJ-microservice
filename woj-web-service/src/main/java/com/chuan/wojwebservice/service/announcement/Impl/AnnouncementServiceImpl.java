package com.chuan.wojwebservice.service.announcement.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.utils.ResultUtils;
import com.chuan.wojmodel.pojo.dto.announcement.AnnouncementAddDTO;
import com.chuan.wojmodel.pojo.dto.announcement.AnnouncementSearchDTO;
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

        BeanUtils.copyProperties(announcementAddDTO, announcement);

        boolean save = this.save(announcement);

        if (save) {
            return ResultUtils.success("保存成功");
        } else {
            log.error("AnnouncementServiceImpl---->addAnnouncement---保存公告失败");
            return ResultUtils.error("保存失败");
        }
    }


    @Override
    public BaseResponse<List<AnnouncementTitleVO>> getAnnouncementTitleList() {
        List<AnnouncementTitleVO> announcementTitleVOS = announcementMapper.selectAnnouncementTitleList();
        for (int i = 0; i < announcementTitleVOS.size(); i++) {
            announcementTitleVOS.get(i).setCreateTime(announcementTitleVOS.get(i).getCreateTime());
        }
        return ResultUtils.success(announcementTitleVOS);
    }

    @Override
    public BaseResponse<AnnouncementContentVO> getAnnouncement(Integer id) throws StatusFailException {
        if (id == null || id <= 0) {
            throw new StatusFailException("参数错误");
        }
        return ResultUtils.success(announcementMapper.selectAnnouncementContentVO(id));
    }

    @Override
    public BaseResponse<AnnouncementContentVO> getLastAnnouncement() {
        return ResultUtils.success(announcementMapper.selectLatestAnnouncementContentVO());
    }

    @Override
    public BaseResponse<Page<AnnouncementContentVO>> getAnnouncementList(AnnouncementSearchDTO announcementSearchDTO, Integer current, Integer size) {
        int pageNum = (current == null || current <= 0) ? 1 : current;
        int pageSize = (size == null || size <= 0) ? 10 : size;

        if (announcementSearchDTO == null) {
            announcementSearchDTO = new AnnouncementSearchDTO();
        }

        IPage<Announcement> page = announcementMapper.selectAnnouncementList(
                new Page<>(pageNum, pageSize),
                announcementSearchDTO
        );

        IPage<AnnouncementContentVO> resultPage = page.convert(AnnouncementContentVO::objToVo);

        return ResultUtils.success((Page<AnnouncementContentVO>) resultPage);
    }
}




