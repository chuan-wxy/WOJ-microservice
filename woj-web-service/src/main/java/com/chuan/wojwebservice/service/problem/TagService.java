package com.chuan.wojwebservice.service.problem;


import com.baomidou.mybatisplus.extension.service.IService;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojmodel.pojo.entity.Tag;
import com.chuan.wojmodel.pojo.vo.problem.TagVO;

import java.util.List;

/**
* @author lenovo
* @description 针对表【tag】的数据库操作Service
* @createDate 2024-08-27 12:07:13
*/
public interface TagService extends IService<Tag> {
    BaseResponse<List<TagVO>> getProblemTagList();
}
