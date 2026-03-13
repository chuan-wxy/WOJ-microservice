package com.chuan.wojuserservice.service.impl;
import java.util.Date;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.utils.ResultUtils;
import com.chuan.wojmodel.pojo.dto.user.UserSearchDTO;
import com.chuan.wojmodel.pojo.entity.Problem;
import com.chuan.wojmodel.pojo.entity.User;
import com.chuan.wojmodel.pojo.vo.problem.ProblemTitleVO;
import com.chuan.wojmodel.pojo.vo.user.UserAdminVO;
import com.chuan.wojmodel.pojo.vo.user.UserVO;
import com.chuan.wojuserservice.mapper.UserMapper;
import com.chuan.wojuserservice.service.AdminService;
import com.chuan.wojuserservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: chuan-wxy
 * @Date: 2024/8/18 15:32
 */
@Slf4j
@Service
public class AdminServiceImpl extends ServiceImpl<UserMapper, User> implements AdminService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserService userService;

    /**
     * 根据uuid删除用户
     * @param id
     * @return
     */
    @Override
    public BaseResponse<Void> deleteUserByid(String id, HttpServletRequest request) {


        if(id == null) {
            return ResultUtils.error("uuid 不能为空");
        }

        // todo 判断该用户是否为管理员

        return null;
    }

    @Override
    public BaseResponse<Page<UserAdminVO>> getUserList(UserSearchDTO userSearchDTO) throws StatusFailException {
        if (userSearchDTO == null) throw new StatusFailException("参数错误");

        String id = userSearchDTO.getId();
        String userAccount = userSearchDTO.getUserAccount();
        String userName = userSearchDTO.getUserName();
        String school = userSearchDTO.getSchool();
        String number = userSearchDTO.getNumber();
        Integer gender = userSearchDTO.getGender();
        Integer isDelete = userSearchDTO.getIsDelete();

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(StringUtils.isNotBlank(id), User::getId, id)
                .eq(StringUtils.isNotBlank(userAccount), User::getUserAccount, userAccount)
                .like(StringUtils.isNotBlank(userName), User::getUserName, userName)
                .like(StringUtils.isNotBlank(school), User::getSchool, school)
                .eq(StringUtils.isNotBlank(number), User::getNumber, number)
                .eq(gender != null, User::getGender, gender)
                .eq(isDelete != null, User::getIsDelete, isDelete);

        Page<User> page = this.page(new Page<>(1, 10), lambdaQueryWrapper);

        List<User> userList = page.getRecords();

        Page<UserAdminVO> userAdminVOPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        if (CollUtil.isEmpty(userList)) {
            return ResultUtils.success(userAdminVOPage);
        }

        // 填充信息
        List<UserAdminVO> userAdminVOList = userList.stream().map(user -> {
            UserAdminVO userAdminVO = UserAdminVO.objToVo(user);

            userAdminVO.setRoles(userService.getUserRole(user.getUserAccount()).getData());

            return userAdminVO;
        }).collect(Collectors.toList());

        userAdminVOPage.setRecords(userAdminVOList);

        return ResultUtils.success(userAdminVOPage);
    }
}
