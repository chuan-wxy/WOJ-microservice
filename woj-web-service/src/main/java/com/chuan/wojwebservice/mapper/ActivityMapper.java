package com.chuan.wojwebservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chuan.wojmodel.pojo.entity.Activity;
import com.chuan.wojmodel.pojo.vo.activity.ActivityContentVO;
import com.chuan.wojmodel.pojo.vo.activity.ActivityTitleVO;

import java.util.List;

/**
* @author chuan-wxy
* @description 针对表【activity】的数据库操作Mapper
* @createDate 2024-10-02 19:37:38
* @Entity generator.domain.Activity
*/
public interface ActivityMapper extends BaseMapper<Activity> {

    List<ActivityTitleVO> selectActivityTitleList();

    ActivityContentVO selectActivityContentVO(Integer id);
}




