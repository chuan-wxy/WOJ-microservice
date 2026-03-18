package com.chuan.wojwebservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojmodel.pojo.dto.announcement.AnnouncementAddDTO;
import com.chuan.wojmodel.pojo.dto.announcement.AnnouncementSearchDTO;
import com.chuan.wojmodel.pojo.vo.announcement.AnnouncementContentVO;
import com.chuan.wojmodel.pojo.vo.announcement.AnnouncementTitleVO;
import com.chuan.wojwebservice.service.announcement.AnnouncementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: chuan-wxy
 * @Date: 2024/10/6 19:51
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {
    @Autowired
    AnnouncementService announcementService;

    /** 获取活动标题列表 */
    @GetMapping("/get-announcement-list")
    public BaseResponse<List<AnnouncementTitleVO>> getAnnouncementTitleList() {
        return announcementService.getAnnouncementTitleList();
    }

    /** 获取单个活动文章 */
    @GetMapping("/get-announcement")
    public BaseResponse<AnnouncementContentVO> getAnnouncement(@RequestParam(value = "id") Integer id) throws StatusFailException {
        return announcementService.getAnnouncement(id);
    }

    /** 获取最新活动文章 */
    @GetMapping("/get-last-announcement")
    public BaseResponse<AnnouncementContentVO> getLastAnnouncement() {
        return announcementService.getLastAnnouncement();
    }

    // 管理员接口
    /** 新增活动 */
    @PostMapping("/admin/add")
    public BaseResponse<String> addAnnouncement(@RequestBody AnnouncementAddDTO announcementAddDTO) throws StatusFailException {
        return announcementService.addAnnouncement(announcementAddDTO);
    }

    /** 获取公告列表 */
    @PostMapping("/admin/announcement-list")
    public BaseResponse<Page<AnnouncementContentVO>> getAnnouncementList(@RequestBody AnnouncementSearchDTO announcementSearchDTO,
                                                                         @RequestParam("current") Integer current,
                                                                         @RequestParam("size") Integer size) throws StatusFailException {
        return announcementService.getAnnouncementList(announcementSearchDTO, current, size);
    }

}
