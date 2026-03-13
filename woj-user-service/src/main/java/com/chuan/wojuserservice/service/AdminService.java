package com.chuan.wojuserservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojmodel.pojo.entity.User;
import com.chuan.wojmodel.pojo.vo.user.UserAdminVO;
import com.chuan.wojmodel.pojo.vo.user.UserVO;

import java.util.HashMap;
import java.util.List;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 特殊接口
 *
 * @Author chuan-wxy
 * @Create 2024/8/18 15:34
 */
public interface AdminService extends IService<User> {
    BaseResponse<UserVO> searchUserById(String uuid);

    BaseResponse<List<UserVO>> searchUserByGneder(String gender);

    BaseResponse<Void> deleteUserByid(String uuid, HttpServletRequest request);

    BaseResponse<Page<UserAdminVO>> getUserList();
}
