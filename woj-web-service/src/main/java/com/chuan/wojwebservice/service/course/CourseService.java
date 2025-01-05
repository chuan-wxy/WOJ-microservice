package com.chuan.wojwebservice.service.course;


import com.baomidou.mybatisplus.extension.service.IService;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.utils.CourseTreeBuilder;
import com.chuan.wojmodel.pojo.dto.course.CourseAddDTO;
import com.chuan.wojmodel.pojo.entity.Course;

import java.util.List;
import java.util.Map;

/**
* @author lenovo
* @description 针对表【course】的数据库操作Service
* @createDate 2024-09-25 16:42:58
*/
public interface CourseService extends IService<Course> {

    BaseResponse<String> addCourse(CourseAddDTO courseAddDTO) throws StatusFailException;

    BaseResponse<List<Course>> getCourseByLevel(Integer level);

    BaseResponse<Map<Integer , CourseTreeBuilder.Category>> getCourseList();

    BaseResponse<List<Course>> getFirst();

    BaseResponse<Map<Integer , CourseTreeBuilder.Category>> getSecond(Long id);
}
