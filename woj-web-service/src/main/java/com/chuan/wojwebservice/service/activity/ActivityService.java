package com.chuan.wojwebservice.service.activity;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojmodel.pojo.dto.activity.ActivityAddDTO;
import com.chuan.wojmodel.pojo.entity.Activity;
import com.chuan.wojmodel.pojo.vo.activity.ActivityContentVO;
import com.chuan.wojmodel.pojo.vo.activity.ActivityTitleVO;

import java.util.List;

/**
* @author chuan-wxy
* @description 针对表【activity】的数据库操作Service
* @createDate 2024-10-02 19:37:38
*/
public interface ActivityService extends IService<Activity> {

    BaseResponse<String> addActivity(ActivityAddDTO activityAddDTO) throws StatusFailException;

    BaseResponse<List<ActivityTitleVO>> getActivityList();

    BaseResponse<ActivityContentVO> getActivity(Integer id);
}
