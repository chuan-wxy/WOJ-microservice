package com.chuan.wojwebservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chuan.wojmodel.pojo.dto.announcement.AnnouncementSearchDTO;
import com.chuan.wojmodel.pojo.entity.Announcement;
import com.chuan.wojmodel.pojo.vo.announcement.AnnouncementContentVO;
import com.chuan.wojmodel.pojo.vo.announcement.AnnouncementTitleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author chuan-wxy
* @description 针对表【announcement】的数据库操作Mapper
* @createDate 2024-10-06 19:45:34
* @Entity generator.domain.Announcement
*/
public interface AnnouncementMapper extends BaseMapper<Announcement> {

    List<AnnouncementTitleVO> selectAnnouncementTitleList();

    AnnouncementContentVO selectAnnouncementContentVO(Integer id);

    AnnouncementContentVO selectLatestAnnouncementContentVO();

    IPage<Announcement> selectAnnouncementList(Page<Object> objectPage, @Param("searchDTO") AnnouncementSearchDTO searchDTO);
}




