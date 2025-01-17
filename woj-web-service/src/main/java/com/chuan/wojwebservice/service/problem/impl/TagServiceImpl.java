package com.chuan.wojwebservice.service.problem.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.utils.ResultUtils;
import com.chuan.wojmodel.pojo.entity.Tag;
import com.chuan.wojmodel.pojo.vo.problem.TagVO;
import com.chuan.wojwebservice.mapper.TagMapper;
import com.chuan.wojwebservice.service.problem.TagService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author chuan-wxy
* @description 针对表【tag】的数据库操作Service实现
* @createDate 2024-08-27 12:07:13
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService {

    @Override
    public BaseResponse<List<TagVO>> getProblemTagList() {
        List<Tag> list = this.list();
        List<TagVO> tagVOList = new ArrayList<>();
        for (Tag tag : list) {
            TagVO tagVO = new TagVO();
            BeanUtils.copyProperties(tag, tagVO);
            tagVOList.add(tagVO);
        }
        return ResultUtils.success(tagVOList);
    }
}




