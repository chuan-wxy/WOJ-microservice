package com.chuan.wojuserservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojmodel.pojo.dto.user.UserSearchDTO;
import com.chuan.wojmodel.pojo.entity.User;
import com.chuan.wojmodel.pojo.vo.user.UserAdminVO;
import com.chuan.wojmodel.pojo.vo.user.UserVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 特殊接口
 *
 * @Author chuan-wxy
 * @Create 2024/8/18 15:34
 */
public interface AdminService extends IService<User> {

    BaseResponse<Void> deleteUserByid(String uuid) throws StatusFailException;

    BaseResponse<Page<UserAdminVO>> getUserList(UserSearchDTO userSearchDTO, Integer current, Integer size) throws StatusFailException;

}
