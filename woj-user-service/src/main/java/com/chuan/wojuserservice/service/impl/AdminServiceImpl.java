package com.chuan.wojuserservice.service.impl;
import java.util.Date;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.utils.ResultUtils;
import com.chuan.wojmodel.pojo.dto.announcement.AnnouncementSearchDTO;
import com.chuan.wojmodel.pojo.dto.role.UserRoleDTO;
import com.chuan.wojmodel.pojo.dto.user.UserSearchDTO;
import com.chuan.wojmodel.pojo.entity.User;
import com.chuan.wojmodel.pojo.vo.user.UserAdminVO;
import com.chuan.wojuserservice.mapper.UserMapper;
import com.chuan.wojuserservice.mapper.UserRoleMapper;
import com.chuan.wojuserservice.service.AdminService;
import com.chuan.wojuserservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
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
    UserRoleMapper userRoleMapper;

    /**
     * 根据uuid删除用户
     * @param id
     * @return
     */
    @Override
    public BaseResponse<Void> deleteUserByid(String id) throws StatusFailException {
        if(id.isBlank()) throw new StatusFailException("id为空");

        Long userId = Long.parseLong(id);

        if (this.removeById(userId)) return ResultUtils.success();
        else return ResultUtils.error("注销失败");
    }

    @Override
    public BaseResponse<Page<UserAdminVO>> getUserList(UserSearchDTO userSearchDTO, Integer current, Integer size) throws StatusFailException {
        if (userSearchDTO == null) {
            userSearchDTO = new UserSearchDTO();
        }

        IPage<User> page = baseMapper.selectUserListWithDeleted(new Page<>(current, size), userSearchDTO);

        Page<UserAdminVO> userAdminVOPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        if (CollUtil.isEmpty(page.getRecords())) {
            return ResultUtils.success(userAdminVOPage);
        }

        List<Long> uids = page.getRecords().stream()
                .map(User::getId)
                .collect(Collectors.toList());

        Map<Long, List<String>> rolesMap = this.batchGetUserRoles(uids);

        List<UserAdminVO> userAdminVOList = page.getRecords().stream().map(user -> {
            UserAdminVO vo = UserAdminVO.objToVo(user);

            List<String> userRoles = rolesMap.getOrDefault(user.getId(), new ArrayList<>());
            vo.setRoles(userRoles);

            return vo;
        }).collect(Collectors.toList());

        userAdminVOPage.setRecords(userAdminVOList);

        return ResultUtils.success(userAdminVOPage);
    }

    public Map<Long, List<String>> batchGetUserRoles(List<Long> uids) {
        if (CollUtil.isEmpty(uids)) {
            return new HashMap<>();
        }

        List<UserRoleDTO> userRoleDTOs = userRoleMapper.selectRoleNamesByUids(uids);

        return userRoleDTOs.stream()
                .collect(Collectors.groupingBy(
                        UserRoleDTO::getUid,
                        Collectors.mapping(UserRoleDTO::getRoleName, Collectors.toList())
                ));
    }
}
