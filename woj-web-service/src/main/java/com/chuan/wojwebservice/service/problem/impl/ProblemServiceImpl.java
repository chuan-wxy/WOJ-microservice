package com.chuan.wojwebservice.service.problem.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.exception.StatusSystemErrorException;
import com.chuan.wojcommon.utils.DataExtractorUtil;
import com.chuan.wojcommon.utils.ResultUtils;
import com.chuan.wojmodel.pojo.dto.problem.ProblemAddDTO;
import com.chuan.wojmodel.pojo.dto.problem.ProblemUpdateDTO;
import com.chuan.wojmodel.pojo.entity.Problem;
import com.chuan.wojmodel.pojo.entity.ProblemInformation;
import com.chuan.wojmodel.pojo.entity.ProblemTag;
import com.chuan.wojmodel.pojo.entity.Tag;
import com.chuan.wojmodel.pojo.vo.problem.ProblemTitleVO;
import com.chuan.wojmodel.pojo.vo.problem.ProblemVO;
import com.chuan.wojwebservice.manager.ProblemManager;
import com.chuan.wojwebservice.mapper.ProblemInformationMapper;
import com.chuan.wojwebservice.mapper.ProblemMapper;
import com.chuan.wojwebservice.mapper.ProblemTagMapper;
import com.chuan.wojwebservice.mapper.TagMapper;
import com.chuan.wojwebservice.service.problem.ProblemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem>
        implements ProblemService {
    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ProblemTagMapper problemTagMapper;

    @Autowired
    private ProblemManager problemManager;

    @Autowired
    private ProblemInformationMapper problemInformationMapper;


    @Autowired
    DataExtractorUtil dataExtractorUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> addProblem(ProblemAddDTO problemAddDTO) throws StatusFailException, StatusSystemErrorException {

        problemManager.validateSubmitInfo(problemAddDTO);

        String author = problemAddDTO.getAuthor();
        String source = problemAddDTO.getSource();

        Problem problem = new Problem();
        BeanUtils.copyProperties(problemAddDTO, problem);
        problem.setTagList(problemAddDTO.getTagList().toString());

        int row = problemMapper.insert(problem);
        if (row != 1) {
            log.info("[addProblem] row非1,插入Problem失败");
            return ResultUtils.error("插入失败");
        }

        ProblemInformation problemInformation = new ProblemInformation();
        problemInformationMapper.insert(problemInformation);

        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("problemId", problemAddDTO.getProblemId());
        problem = problemMapper.selectOne(queryWrapper);
        long problemId = problem.getId();

        for (String tag : problemAddDTO.getTagList()) {
            ProblemTag problemTag = new ProblemTag();

            Long tid = tagMapper.seleceIdByName(tag);

            problemTag.setPid(problemId);
            problemTag.setTid(tid);

            row = problemTagMapper.insert(problemTag);
            if (row != 1) {
                log.info("[addProblem] 插入ProblemTag失败");
                throw new StatusSystemErrorException("插入失败");
            }
        }
        return ResultUtils.success("插入成功");
    }

    @Override
    public BaseResponse<String> addTag(String tagName) throws StatusFailException {
        problemManager.validateTag(tagName);

        Tag tag = new Tag();
        tag.setName(tagName);

        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",tagName);
        Tag tag1 = tagMapper.selectOne(queryWrapper);
        if(tag1!=null) {
            return null;
        }

        int row = tagMapper.insert(tag);
        if (row != 1) {
            log.info("ProblemServiceImpl---->addTag---插入tag失败");
            throw new StatusFailException("添加失败");
        }
        return ResultUtils.success("添加成功");
    }

    @Override
    public BaseResponse<Page<ProblemTitleVO>> getProblemTitle(Integer current, Integer size) throws StatusFailException {
        if(current==null || size==null) {
            log.info("ProblemServiceImpl---->getProblemTitle---current和size为空");
            throw new StatusFailException("current和size不能为空");
        }
        Page<Problem> page = this.page(new Page<>(current, size));
        return ResultUtils.success(problemManager.getProblemTitleVOPage(page));
    }

    @Override
    public BaseResponse<ProblemVO> getProblem(Long id) throws StatusFailException {
        if (id == null) {
            throw new StatusFailException("id为空");
        }
        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);

        Problem problem = problemMapper.selectOne(queryWrapper);
        ProblemVO problemVO = new ProblemVO();

        String tagStr = problem.getTagList();
        if (tagStr.startsWith("[") && tagStr.endsWith("]")) {
            // 去除字符串的首尾字符
            tagStr = tagStr.substring(1, tagStr.length() - 1);
        }

        List<String> tagList = Arrays.asList(tagStr.split(", "));
        BeanUtils.copyProperties(problem, problemVO);
        problemVO.setTagList(tagList);
        return ResultUtils.success(problemVO);
    }

    @Override
    public BaseResponse<Page<ProblemTitleVO>> searchProblemTitle(Integer current, Integer size, String text) throws StatusFailException {
        if(current==null||size==null) {
            throw new StatusFailException("current和size不能为空");
        }
        dataExtractorUtil.doExtraction(text);
        List<String> difficulty = dataExtractorUtil.getDifficulties();
        List<String> tags = dataExtractorUtil.getTags();
        List<String> keyword = dataExtractorUtil.getKeywords();
        // 题库页普通查询
        Page<Problem> page = this.page(new Page<>(current, size),
                problemManager.getQueryWrapper(keyword,tags,difficulty));
        return ResultUtils.success(problemManager.getProblemTitleVOPage(page));
    }

    @Override
    public BaseResponse<IPage<ProblemTitleVO>> searchProblemTitleTwo(Integer current, Integer size, Long id, String tags, String difficulty, String title) throws StatusFailException {
        if(current==null||size==null) {
            throw new StatusFailException("current和size不能为空");
        }

        Page<Problem> page = this.page(new Page<>(current, size),
                problemManager.getQueryWrapperTwo(id,tags,difficulty,title));
        return ResultUtils.success(problemManager.getProblemTitleVOPage(page));
    }

    @Override
    public BaseResponse<String> updateProblem(ProblemUpdateDTO problemUpdateDTO) throws StatusFailException {
        if(problemUpdateDTO == null || problemUpdateDTO.getId() == null) {
            throw new StatusFailException("id不能为空");
        }
        long pid = problemUpdateDTO.getId();
        Problem problem = problemMapper.selectById(pid);
        if(problem == null) {
            throw new StatusFailException("该id不存在");
        }

        UpdateWrapper<Problem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",pid);

        Problem newProblem = new Problem();
        BeanUtils.copyProperties(problemUpdateDTO,newProblem);
        newProblem.setTagList(problemUpdateDTO.getTagList().toString());
        newProblem.setUpdateTime(DateTime.now());
        int row = problemMapper.update(newProblem,updateWrapper);
        if(row != 1) {
            log.info("ProblemServiceImpl--->updateProblem()---更新题目信息失败");
            return ResultUtils.error("更新失败");
        }

        return ResultUtils.success("更新成功");
    }

    @Override
    public BaseResponse<ProblemInformation> getProblemInformation(Long id) {
        if(id == null) {
            return ResultUtils.error("id为空");
        }
        QueryWrapper<ProblemInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pid", id);
        ProblemInformation problemInformation = problemInformationMapper.selectOne(queryWrapper);
        if(problemInformation == null) {
            return ResultUtils.error("id错误");
        }
        return ResultUtils.success(problemInformation);
    }
}




