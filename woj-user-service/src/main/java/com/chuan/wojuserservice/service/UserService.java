package com.chuan.wojuserservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojmodel.pojo.dto.user.*;
import com.chuan.wojmodel.pojo.entity.User;
import com.chuan.wojmodel.pojo.vo.user.UserLoginVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 公共接口
 *
 * @Author chuan-wxy
 * @Create 2024/8/14 20:25
 */
public interface UserService extends IService<User> {

    BaseResponse<Void> register(UserRegisterDTO userAddDTO) throws StatusFailException;

    BaseResponse<UserLoginVO> login(UserLoginDTO userLoginDTO, HttpServletRequest request);

    BaseResponse<Void> updateProfile(UserProfileDTO userProfileDTO);

    BaseResponse<Void> logout(HttpServletRequest request);

    BaseResponse<Void> getLogoutCode(UserLogoutDTO userLogoutDTO);

    BaseResponse<Void> logoutForever(UserLogoutDTO userLogoutDTO);

    BaseResponse<Void> editPassword(UserPasswordDTO userPasswordDTO, HttpServletRequest request) throws StatusFailException;

    BaseResponse<List<String>> getUserRole(String userAccount);

    BaseResponse<UserLoginVO> getLoginUser(HttpServletRequest request) throws StatusFailException;

    BaseResponse<Boolean> checkJWT(String JWT);
}
