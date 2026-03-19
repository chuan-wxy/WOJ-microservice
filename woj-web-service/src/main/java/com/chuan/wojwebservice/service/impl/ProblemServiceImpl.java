package com.chuan.wojwebservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.exception.StatusSystemErrorException;
import com.chuan.wojcommon.utils.DataExtractorUtil;
import com.chuan.wojcommon.utils.ResultUtils;
import com.chuan.wojmodel.pojo.dto.problem.ProblemAddDTO;
import com.chuan.wojmodel.pojo.dto.problem.ProblemSearchDTO;
import com.chuan.wojmodel.pojo.dto.problem.ProblemTagDTO;
import com.chuan.wojmodel.pojo.dto.problem.ProblemUpdateDTO;
import com.chuan.wojmodel.pojo.entity.*;
import com.chuan.wojmodel.pojo.vo.problem.ProblemTitleVO;
import com.chuan.wojmodel.pojo.vo.problem.ProblemVO;
import com.chuan.wojwebservice.manager.ProblemManager;
import com.chuan.wojwebservice.mapper.ProblemMapper;
import com.chuan.wojwebservice.mapper.ProblemStatsMapper;
import com.chuan.wojwebservice.mapper.ProblemTagMapper;
import com.chuan.wojwebservice.mapper.TagMapper;
import com.chuan.wojwebservice.service.ProblemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
    private ProblemStatsMapper problemStatsMapper;

    @Autowired
    DataExtractorUtil dataExtractorUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> addProblem(ProblemAddDTO problemAddDTO) throws StatusFailException, StatusSystemErrorException {
        Problem problem = new Problem();
        BeanUtils.copyProperties(problemAddDTO, problem);

        if(problemMapper.insert(problem) != 1) throw new StatusFailException("插入数据库失败");

        for(String tagStr : problemAddDTO.getTagList()) {
            Tag tag = tagMapper.selectByName(tagStr);
            if(tag == null) {
                tag = new Tag();
                tag.setName(tagStr);
                tagMapper.insert(tag);
            }

            ProblemTag problemTag = new ProblemTag();

            problemTag.setPid(problem.getId());
            problemTag.setTid(tag.getId());

            problemTagMapper.insert(problemTag);
        }

        ProblemStats problemStats = new ProblemStats();
        problemStats.setPid(problem.getId());
        problemStatsMapper.insert(problemStats);

        return ResultUtils.success("插入成功");
    }

    @Override
    public BaseResponse<String> addTag(String tagName) throws StatusFailException {
        problemManager.validateTag(tagName);

        Tag tag = new Tag();
        tag.setName(tagName);

        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", tagName);
        Tag tag1 = tagMapper.selectOne(queryWrapper);
        if (tag1 != null) {
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
    public BaseResponse<ProblemVO> getProblem(String id) throws StatusFailException {
        Long pid;
        try {
            pid = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new StatusFailException("ID格式错误，必须为数字");
        }

        Problem problem = problemMapper.selectById(pid);

        if (problem == null) {
            throw new StatusFailException("根据ID：" + id + " 未找到对应的问题");
        }

        ProblemVO problemVO = ProblemVO.objToVo(problem);

        List<Long> pids = new ArrayList<>();
        pids.add(pid);

        Map<Long, List<String>> tagsMap = this.batchGetProblemTags(pids);

        List<String> problemTags = tagsMap.getOrDefault(problem.getId(), new ArrayList<>());
        problemVO.setTagList(problemTags);

        return ResultUtils.success(problemVO);
    }

    @Override
    public BaseResponse<ProblemStats> getProblemStatistics(String id) throws StatusFailException {
        Long pid;
        try {
            pid = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new StatusFailException("ID格式错误，必须为数字");
        }

        ProblemStats problemStats = problemStatsMapper.getByPid(pid);

        return ResultUtils.success(problemStats);
    }

//    @Override
//    public BaseResponse<Page<ProblemTitleVO>> searchProblemTitle(Integer current, Integer size, String text) throws StatusFailException {
//        if (current == null || size == null) {
//            throw new StatusFailException("current和size不能为空");
//        }
//        dataExtractorUtil.doExtraction(text);
//        List<String> difficulty = dataExtractorUtil.getDifficulties();
//        List<String> tags = dataExtractorUtil.getTags();
//        List<String> keyword = dataExtractorUtil.getKeywords();
//        // 题库页普通查询
//        Page<Problem> page = this.page(new Page<>(current, size),
//                problemManager.getQueryWrapper(keyword, tags, difficulty));
//        return ResultUtils.success(problemManager.getProblemTitleVOPage(page));
//    }

//    @Override
//    public BaseResponse<IPage<ProblemTitleVO>> searchProblemTitleTwo(Integer current, Integer size, Long id, String tags, String difficulty, String title) throws StatusFailException {
//        if (current == null || size == null) {
//            throw new StatusFailException("current和size不能为空");
//        }
//
//        Page<Problem> page = this.page(new Page<>(current, size),
//                problemManager.getQueryWrapperTwo(id, tags, difficulty, title));
//        return ResultUtils.success(problemManager.getProblemTitleVOPage(page));
//    }

    @Override
    public BaseResponse<String> updateProblem(ProblemUpdateDTO problemUpdateDTO) throws StatusFailException {
        if (problemUpdateDTO == null || problemUpdateDTO.getId() == null) {
            throw new StatusFailException("id不能为空");
        }
        long pid = problemUpdateDTO.getId();
        Problem problem = problemMapper.selectById(pid);
        if (problem == null) {
            throw new StatusFailException("该id不存在");
        }

        UpdateWrapper<Problem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", pid);

        Problem newProblem = new Problem();
        BeanUtils.copyProperties(problemUpdateDTO, newProblem);
        // todo
        // newProblem.setTagList(problemUpdateDTO.getTagList().toString());
        newProblem.setUpdateTime(DateTime.now());
        int row = problemMapper.update(newProblem, updateWrapper);
        if (row != 1) {
            log.info("ProblemServiceImpl--->updateProblem()---更新题目信息失败");
            return ResultUtils.error("更新失败");
        }

        return ResultUtils.success("更新成功");
    }


    // todo 实现题目标签查找
    @Override
    public BaseResponse<Page<ProblemTitleVO>> getProblemTitleList(ProblemSearchDTO problemSearchDTO, Integer current, Integer size) {
        int pageNum = (current == null || current <= 0) ? 1 : current;
        int pageSize = (size == null || size <= 0) ? 10 : size;

        if (problemSearchDTO == null) {
            problemSearchDTO = new ProblemSearchDTO();
        }

        Long id = problemSearchDTO.getId();
        String problemId = problemSearchDTO.getProblemId();
        String title = problemSearchDTO.getTitle();
        String author = problemSearchDTO.getAuthor();
        String source = problemSearchDTO.getSource();
        Integer difficulty = problemSearchDTO.getDifficulty();

        LambdaQueryWrapper<Problem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .select(Problem::getId, Problem::getProblemId, Problem::getTitle, Problem::getAuthor, Problem::getSource, Problem::getDifficulty)
                .eq(id != null, Problem::getId, id)
                .like(StringUtils.isNotBlank(problemId), Problem::getProblemId, problemId)
                .like(StringUtils.isNotBlank(title), Problem::getTitle, title)
                .like(StringUtils.isNotBlank(author), Problem::getAuthor, author)
                .like(StringUtils.isNotBlank(source), Problem::getSource, source)
                .eq(difficulty != null, Problem::getDifficulty, difficulty)
                .orderByDesc(Problem::getCreateTime);

        IPage<Problem> page = problemMapper.selectPage(new Page<>(pageNum, pageSize), lambdaQueryWrapper);

        Page<ProblemTitleVO> problemTitleVOPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        if (CollUtil.isEmpty(page.getRecords())) return ResultUtils.success(problemTitleVOPage);

        // 查询每个题目的tag
        List<Long> pids = page.getRecords().stream().map(Problem::getId).collect(Collectors.toList());

        Map<Long, List<String>> tagsMap = this.batchGetProblemTags(pids);

        List<ProblemTitleVO> problemTitleVOList = page.getRecords().stream().map(problem -> {
            ProblemTitleVO vo = ProblemTitleVO.objToVo(problem);

            List<String> problemTags = tagsMap.getOrDefault(problem.getId(), new ArrayList<>());
            vo.setTagList(problemTags);

            return vo;
        }).collect(Collectors.toList());

        problemTitleVOPage.setRecords(problemTitleVOList);

        return ResultUtils.success(problemTitleVOPage);
    }

    public Map<Long, List<String>> batchGetProblemTags(List<Long> pids) {
        if (CollUtil.isEmpty(pids)) {
            return new HashMap<>();
        }

        List<ProblemTagDTO> problemTagDTOS = problemMapper.selectProblemTagsByPids(pids);

        return problemTagDTOS.stream()
                .collect(Collectors.groupingBy(
                        ProblemTagDTO::getPid,
                        Collectors.mapping(ProblemTagDTO::getProblemName, Collectors.toList())
                ));
    }
}

