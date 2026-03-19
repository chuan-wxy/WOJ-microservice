package com.chuan.wojwebservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chuan.wojmodel.pojo.dto.problem.ProblemTagDTO;
import com.chuan.wojmodel.pojo.entity.Problem;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
* @author chuan-wxy
* @description 针对表【problem】的数据库操作Mapper
* @createDate 2024-08-26 16:25:01
* @Entity generator.domain.Problem
*/
public interface ProblemMapper extends BaseMapper<Problem> {

    List<ProblemTagDTO> selectProblemTagsByPids(@Param("pids")List<Long> pids);
}
