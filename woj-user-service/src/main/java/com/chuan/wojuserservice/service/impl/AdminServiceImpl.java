package com.chuan.wojuserservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.utils.ResultUtils;
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

    @Override
    public BaseResponse<UserVO> searchUserById(String uuid) {
        if (uuid == null) {
            log.info("查询失败");
            return ResultUtils.error("查询失败");
        }

        User user = userMapper.selectById(uuid);

        if (user == null) {
            return ResultUtils.error("没有找到该用户");
        }

        UserVO userVO = new UserVO();

        BeanUtils.copyProperties(user,userVO);
        return ResultUtils.success(userVO);
    }

    @Override
    public BaseResponse<List<UserVO>> searchUserByGneder(String gender) {
        Set<String> allowedGenders = new HashSet<>(Arrays.asList("男", "女", "保密"));

        if (!allowedGenders.contains(gender)) {
            return ResultUtils.error("请输入：男、女或保密");
        }
        QueryWrapper queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("gender",gender);

        List<User> list = userMapper.selectList(queryWrapper);
        List<UserVO> userVOList = new ArrayList<UserVO>();
        BeanUtils.copyProperties(list,userVOList);

        return ResultUtils.success(userVOList);
    }

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
    public BaseResponse<Page<UserAdminVO>> getUserList() {

        Page<User> page = this.page(new Page<>(1, 10));

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

        for(UserAdminVO item : userAdminVOList) {
            System.out.println(item.getId());
        }

        return ResultUtils.success(userAdminVOPage);
    }
}
