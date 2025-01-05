package com.chuan.wojuserservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojmodel.pojo.dto.user.UserLoginDTO;
import com.chuan.wojmodel.pojo.dto.user.UserLogoutDTO;
import com.chuan.wojmodel.pojo.dto.user.UserProfileDTO;
import com.chuan.wojmodel.pojo.dto.user.UserRegisterDTO;
import com.chuan.wojmodel.pojo.entity.User;
import com.chuan.wojmodel.pojo.vo.user.UserLoginVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * 公共接口
 *
 * @Author chuan-wxy
 * @Create 2024/8/14 20:25
 */
public interface UserService extends IService<User> {

    BaseResponse<Void> register(UserRegisterDTO userAddDTO) throws StatusFailException;

    BaseResponse<UserLoginVO> login(UserLoginDTO userLoginDTO, HttpServletRequest request, HttpServletResponse response);

    BaseResponse<Void> updateProfile(UserProfileDTO userProfileDTO);

    BaseResponse<Void> logout(UserLogoutDTO userLogoutDTO);

    BaseResponse<Void> getLogoutCode(UserLogoutDTO userLogoutDTO);

    BaseResponse<Void> logoutForever(UserLogoutDTO userLogoutDTO);

    BaseResponse<List<String>> getUserRole(String userAccount);

    BaseResponse<UserLoginVO> getLoginUser(HttpServletRequest request) throws StatusFailException;

    User getUserByAccount(String account);

    BaseResponse<Boolean> checkJWT(String JWT);
}
