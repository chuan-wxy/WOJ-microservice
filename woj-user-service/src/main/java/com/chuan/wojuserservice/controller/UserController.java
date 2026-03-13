package com.chuan.wojuserservice.controller;

import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojmodel.pojo.dto.user.*;
import com.chuan.wojmodel.pojo.vo.user.UserLoginVO;
import com.chuan.wojuserservice.service.EmailService;
import com.chuan.wojuserservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    EmailService emailService;

    /** 获取验证码 */
    @GetMapping("/get-register-code")
    public BaseResponse<String> getRegisterCode(@RequestParam(value = "email", required = true) String email) {
        return emailService.getCaptchaCode(email,null);
    }

    /** 用户注册 */
    @PostMapping("/register")
    public BaseResponse<Void> register(@RequestBody UserRegisterDTO userRegisterDTO) throws StatusFailException {
        return userService.register(userRegisterDTO);
    }

    /** 登录 */
    @PostMapping("/login")
    public BaseResponse<UserLoginVO> login(@Valid @RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {
        return userService.login(userLoginDTO, request);
    }

    /** 修改用户信息 */
    @PostMapping("/update-profile")
    public BaseResponse<Void> updateProfile(@RequestBody UserProfileDTO userProfileDTO) {
        return userService.updateProfile(userProfileDTO);
    }

    /** 注销获取验证码 */
    @PostMapping("/get-logout-code")
    public BaseResponse<Void> getLogoutCode(@RequestBody UserLogoutDTO userLogoutDTO) {
        return userService.getLogoutCode(userLogoutDTO);
    }

    /** 注销账号 */
    @PostMapping("/logout-forever")
    public BaseResponse<Void> logoutForever(@RequestBody UserLogoutDTO userLogoutDTO) {
        return userService.logoutForever(userLogoutDTO);
    }

    @PostMapping("/edit-password")
    public BaseResponse<Void> editPassword(@Valid @RequestBody UserPasswordDTO userPasswordDTO, HttpServletRequest request) throws StatusFailException {
        return userService.editPassword(userPasswordDTO, request);
    }

    /** 退出登录 */
    @PostMapping("/logout")
    public BaseResponse<Void> logout(HttpServletRequest request) {
        return userService.logout(request);
    }

    /** 获取用户角色 */
    @PostMapping("/get-role")
    public BaseResponse<List<String>> getRole(@RequestParam(value = "userAccount") String userAccount) {
        return userService.getUserRole(userAccount);
    }

    /** 获取用户 */
    @PostMapping("/get-loginuser")
    public BaseResponse<UserLoginVO> getLoginUser(HttpServletRequest request) throws StatusFailException {
        return userService.getLoginUser(request);
    }

    /**
     * 检查JWT令牌是否过期
     *  true 过期
     */
    @GetMapping("/check-jwt")
    public BaseResponse<Boolean> checkJWT(@RequestParam(value = "JWT") String JWT) {
        return userService.checkJWT(JWT);
    }

}
