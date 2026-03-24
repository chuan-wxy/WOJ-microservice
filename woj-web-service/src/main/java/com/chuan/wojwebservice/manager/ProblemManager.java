package com.chuan.wojwebservice.manager;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojmodel.pojo.dto.problem.ProblemAddDTO;
import com.chuan.wojmodel.pojo.entity.Problem;
import com.chuan.wojmodel.pojo.entity.Tag;
import com.chuan.wojmodel.pojo.vo.problem.ProblemTitleVO;
import com.chuan.wojwebservice.mapper.ProblemTagMapper;
import com.chuan.wojwebservice.mapper.TagMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: chuan-wxy
 * @Date: 2024/8/27 11:53
 * @Description:
 */
@Slf4j
@Component
public class ProblemManager {

    public void validateTag(String tagName) throws StatusFailException {
        if (tagName == null) {
            log.debug("ProblemServiceImpl---->addProblem()->ProblemManager.validateTag---tag为空");
            throw new StatusFailException("tag为空");
        }
        if (tagName.isBlank()) {
            throw new StatusFailException("tag名为空");
        }
    }

}

















