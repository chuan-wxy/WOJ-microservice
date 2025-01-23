package com.chuan.wojwebservice.service.problem;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.exception.StatusSystemErrorException;
import com.chuan.wojmodel.pojo.dto.problem.ProblemAddDTO;
import com.chuan.wojmodel.pojo.dto.problem.ProblemUpdateDTO;
import com.chuan.wojmodel.pojo.entity.Problem;
import com.chuan.wojmodel.pojo.entity.ProblemInformation;
import com.chuan.wojmodel.pojo.vo.problem.ProblemTitleVO;
import com.chuan.wojmodel.pojo.vo.problem.ProblemVO;
/**
* @author chuan-wxy
* @description 针对表【problem】的数据库操作Service
* @createDate 2024-08-26 16:25:01
*/
public interface ProblemService extends IService<Problem> {

    BaseResponse<String> addProblem(ProblemAddDTO ProblemAddDTO) throws StatusFailException, StatusSystemErrorException;

    BaseResponse<String> addTag(String tagName) throws StatusFailException;

    BaseResponse<Page<ProblemTitleVO>> getProblemTitle(Integer current, Integer size) throws StatusFailException;

    BaseResponse<ProblemVO> getProblem(Long id) throws StatusFailException;

    BaseResponse<Page<ProblemTitleVO>> searchProblemTitle(Integer current, Integer size, String text) throws StatusFailException;

    BaseResponse<IPage<ProblemTitleVO>> searchProblemTitleTwo(Integer current, Integer size, Long id, String tags, String difficulty, String title) throws StatusFailException;

    BaseResponse<String> updateProblem(ProblemUpdateDTO problemUpdateDTO) throws StatusFailException;

    BaseResponse<ProblemInformation> getProblemInformation(Long id);
}
