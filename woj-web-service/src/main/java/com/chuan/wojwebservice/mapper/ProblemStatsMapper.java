package com.chuan.wojwebservice.mapper;

import com.chuan.wojmodel.pojo.entity.ProblemStats;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 31539
* @description 针对表【problem_stats】的数据库操作Mapper
* @createDate 2026-03-19 15:19:20
* @Entity com.chuan.wojmodel.pojo.entity.ProblemStats
*/
public interface ProblemStatsMapper extends BaseMapper<ProblemStats> {

    ProblemStats getByPid(long pid);
}




