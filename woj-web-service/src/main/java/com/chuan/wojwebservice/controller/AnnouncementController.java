package com.chuan.wojwebservice.controller;

import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojmodel.pojo.dto.announcement.AnnouncementAddDTO;
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

    /**
     * 新增活动
     * @param announcementAddDTO
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<String> addAnnouncement(@RequestBody AnnouncementAddDTO announcementAddDTO) throws StatusFailException {
        return announcementService.addAnnouncement(announcementAddDTO);
    }

    /**
     * 获取活动标题列表
     * @return
     */
    @GetMapping("/get-announcement-list")
    public BaseResponse<List<AnnouncementTitleVO>> getAnnouncementList() {
        return announcementService.getAnnouncementList();
    }

    /**
     * 获取单个活动文章
     * @return
     */
    @GetMapping("/get-announcement")
    public BaseResponse<AnnouncementContentVO> getAnnouncement(@RequestParam(value = "id") Integer id) throws StatusFailException {
        return announcementService.getAnnouncement(id);
    }
}
