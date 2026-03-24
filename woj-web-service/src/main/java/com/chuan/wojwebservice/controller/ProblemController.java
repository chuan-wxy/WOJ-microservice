package com.chuan.wojwebservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.exception.StatusSystemErrorException;
import com.chuan.wojmodel.pojo.dto.problem.ProblemAddDTO;
import com.chuan.wojmodel.pojo.dto.problem.ProblemSearchDTO;
import com.chuan.wojmodel.pojo.dto.problem.ProblemUpdateDTO;
import com.chuan.wojmodel.pojo.vo.problem.ProblemTitleVO;
import com.chuan.wojmodel.pojo.vo.problem.ProblemVO;
import com.chuan.wojmodel.pojo.vo.problem.TagVO;
import com.chuan.wojmodel.pojo.vo.problemStats.ProblemStatsVO;
import com.chuan.wojserviceclient.service.UserFeignClient;
import com.chuan.wojwebservice.service.ProblemService;
import com.chuan.wojwebservice.service.TagService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题目接口（包括题目标题等相关属性）
 *
 * @Author: chuan-wxy
 * @Date: 2024/8/26 15:03
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/problem")
public class ProblemController {
    @Autowired
    private ProblemService problemService;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserFeignClient userFeignClient;

    @GetMapping("/testu")
    public String test() {
        System.out.println("asd");
        return userFeignClient.test();
    }

    /**
     * 新增题目标签
     * @param tagName
     * @return
     */
    @PostMapping("/add-tag")
    public BaseResponse<String> addTag(String tagName) throws StatusFailException, StatusSystemErrorException {
        return problemService.addTag(tagName);
    }

    /**
     * 新增题目
     * @param problemAddDTO
     * @return
     */
    @PostMapping("/add-problem")
    public BaseResponse<String> addProblem(@Valid @RequestBody ProblemAddDTO problemAddDTO) throws StatusFailException, StatusSystemErrorException {
        return problemService.addProblem(problemAddDTO);
    }

    /**
     * 获取题目详细信息
     *
     * @param id
     * @return
     * @throws StatusFailException
     */
    @GetMapping("/get-problem")
    public BaseResponse<ProblemVO> getProblem(@NotBlank @RequestParam(value = "id") String id) throws StatusFailException {
        return problemService.getProblem(id);
    }

    /**
     * 获取题目提交统计信息
     *
     * @param id
     * @return
     * @throws StatusFailException
     */

    @GetMapping("/get-probleminformation")
    public BaseResponse<ProblemStatsVO> getProblemStatistics(@NotBlank @RequestParam(value = "id") String id) throws StatusFailException {
        return problemService.getProblemStatistics(id);
    }

    @PostMapping("/problemtitle-list")
    public BaseResponse<Page<ProblemTitleVO>> getProblemTitleList(@RequestBody ProblemSearchDTO problemSearchDTO,
                                                                  @RequestParam("current") Integer current,
                                                                  @RequestParam("size") Integer size ) {
        return problemService.getProblemTitleList(problemSearchDTO, current, size);
    }

    @GetMapping("/get-problemtaglist")
    public BaseResponse<List<TagVO>> getProblemTagList() {
        return tagService.getProblemTagList();
    }

    @PostMapping("/update-problem")
    public BaseResponse<String> updateProblem(@RequestBody ProblemUpdateDTO problemUpdateDTO) throws StatusFailException, StatusSystemErrorException {
        return problemService.updateProblem(problemUpdateDTO);
    }
}
