package com.chuan.wojwebservice.service.course.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.utils.CourseTreeBuilder;
import com.chuan.wojcommon.utils.ResultUtils;
import com.chuan.wojmodel.pojo.dto.course.CourseAddDTO;
import com.chuan.wojmodel.pojo.entity.Course;
import com.chuan.wojwebservice.manager.CourseManager;
import com.chuan.wojwebservice.mapper.CourseMapper;
import com.chuan.wojwebservice.service.course.CourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* @author chuan-wxy
* @description 针对表【course】的数据库操作Service实现
* @createDate 2024-09-25 16:42:58
*/
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course>
    implements CourseService {
    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseManager courseManager;

    @Override
    public BaseResponse<String> addCourse(CourseAddDTO courseAddDTO) throws StatusFailException {

        courseManager.validateCourse(courseAddDTO);
        // 课程查重
        String name = courseAddDTO.getName();
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",name);
        Course course = this.getOne(queryWrapper);
        if(course != null) {
            throw new StatusFailException("课程名称重复");
        }
        // 检查父节点是否存在
        Long pid = courseAddDTO.getPid();
        if(pid!=null && pid!=0) {
            QueryWrapper<Course> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("id",pid);
            Course course1 = this.getOne(queryWrapper1);
            if(course1 == null) {
                throw new StatusFailException("没有改父级课程");
            }
        }

        Course newCourse = new Course();
        BeanUtils.copyProperties(courseAddDTO,newCourse);
        boolean save = this.save(newCourse);
        if(save) {
            return ResultUtils.success("添加成功");
        } else {
            return ResultUtils.error("添加失败");
        }
    }

    @Override
    public BaseResponse<List<Course>> getCourseByLevel(Integer level) {
        if(level == null) {
            return ResultUtils.error("level不能为空");
        }
        if(level <= 0) {
            return null;
        }
        QueryWrapper queryWrapper = new QueryWrapper<Course>();
        queryWrapper.eq("level",level-1);

        List courseList = courseMapper.selectList(queryWrapper);

        return ResultUtils.success(courseList);
    }

    @Override
    public BaseResponse<Map<Integer , CourseTreeBuilder.Category>> getCourseList() {
        List<Course> list = this.list();
        List<CourseTreeBuilder.Category> categories = new ArrayList<>();
        for (Course course : list) {
            CourseTreeBuilder.Category category = new CourseTreeBuilder.Category(course.getId(),course.getName());
            category.setAvatar(course.getAvatar());
            category.setDescription(course.getDescription());
            categories.add(category);
        }
        Map<Integer, CourseTreeBuilder.Category> categoryMap = CourseTreeBuilder.buildTree(categories,0);
        return ResultUtils.success(categoryMap);
    }

    @Override
    public BaseResponse<List<Course>> getFirst() {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("level",1);
        List<Course> list = this.list(queryWrapper);
        return ResultUtils.success(list);
    }

    @Override
    public BaseResponse<Map<Integer, CourseTreeBuilder.Category>> getSecond(Long id) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pid",id);

        List<Course> list = courseMapper.selectCourseInFirst(id);

        List<CourseTreeBuilder.Category> categories = new ArrayList<>();
        for (Course course : list) {
            CourseTreeBuilder.Category category = new CourseTreeBuilder.Category(course.getId(),course.getName());
            category.setAvatar(course.getAvatar());
            category.setPid(course.getPid());
            category.setDescription(course.getDescription());
            categories.add(category);
        }
        Map<Integer, CourseTreeBuilder.Category> categoryMap = CourseTreeBuilder.buildTree(categories, Math.toIntExact(id));
        return ResultUtils.success(categoryMap);
    }
}




