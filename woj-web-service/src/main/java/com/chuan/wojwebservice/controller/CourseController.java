package com.chuan.wojwebservice.controller;


import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.utils.CourseTreeBuilder;
import com.chuan.wojmodel.pojo.dto.course.CourseAddDTO;
import com.chuan.wojmodel.pojo.entity.Course;
import com.chuan.wojwebservice.service.course.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author: chuan-wxy
 * @Date: 2024/9/25 17:03
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * 新增课程
     * @param courseAddDTO
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<String> addCourse(@RequestBody CourseAddDTO courseAddDTO) throws StatusFailException {
        return courseService.addCourse(courseAddDTO);
    }

    /**
     * 新增课程时，查找其上级课程
     * @param level
     * @return
     * @throws StatusFailException
     */
    @GetMapping("/get-course-by-level")
    public BaseResponse<List<Course>> getCourseByLevel(Integer level) {
        return courseService.getCourseByLevel(level);
    }

    @GetMapping("/get-courseList")
    public BaseResponse<Map<Integer , CourseTreeBuilder.Category >> getCourseList() {
        return courseService.getCourseList();
    }

    @GetMapping("/get-first")
    public BaseResponse<List<Course>> getFirst() {
        return courseService.getFirst();
    }

    @GetMapping("/get-second")
    public BaseResponse<Map<Integer , CourseTreeBuilder.Category>> getSecond(@RequestParam Long id) {
        return courseService.getSecond(id);
    }

}
