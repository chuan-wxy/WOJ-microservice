package com.chuan.wojwebservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.exception.StatusSystemErrorException;
import com.chuan.wojmodel.pojo.dto.problem.ProblemAddDTO;
import com.chuan.wojmodel.pojo.dto.problem.ProblemSearchDTO;
import com.chuan.wojmodel.pojo.dto.problem.ProblemUpdateDTO;
import com.chuan.wojmodel.pojo.entity.Problem;
import com.chuan.wojmodel.pojo.entity.ProblemStats;
import com.chuan.wojmodel.pojo.vo.problem.ProblemTitleVO;
import com.chuan.wojmodel.pojo.vo.problem.ProblemVO;
import com.chuan.wojmodel.pojo.vo.problemStats.ProblemStatsVO;

/**
* @author chuan-wxy
* @description 针对表【problem】的数据库操作Service
* @createDate 2024-08-26 16:25:01
*/
public interface ProblemService extends IService<Problem> {

    BaseResponse<String> addProblem(ProblemAddDTO ProblemAddDTO) throws StatusFailException, StatusSystemErrorException;

    BaseResponse<String> addTag(String tagName) throws StatusFailException;

    BaseResponse<ProblemVO> getProblem(String id) throws StatusFailException;

    BaseResponse<ProblemStatsVO> getProblemStatistics(String id) throws StatusFailException;

    BaseResponse<String> updateProblem(ProblemUpdateDTO problemUpdateDTO) throws StatusFailException;

    BaseResponse<Page<ProblemTitleVO>> getProblemTitleList(ProblemSearchDTO problemSearchDTO, Integer current, Integer size);
}
