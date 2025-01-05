package com.chuan.wojjudgeservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuan.wojjudgeservice.mapper.ProblemUpdateMapper;
import com.chuan.wojjudgeservice.service.ProblemUpdateService;
import com.chuan.wojmodel.pojo.entity.Problem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* @author lenovo
* @description 针对表【problem_submit】的数据库操作Service实现
* @createDate 2024-09-12 12:39:38
*/
@Service
@Slf4j
public class ProblemUpdateServiceImpl extends ServiceImpl<ProblemUpdateMapper, Problem>
    implements ProblemUpdateService {

}




