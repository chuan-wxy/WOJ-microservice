package com.chuan.wojserviceclient.service;

import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.exception.StatusForbiddenException;
import com.chuan.wojcommon.utils.JwtUtil;
import com.chuan.wojcommon.utils.ResultUtils;
import com.chuan.wojmodel.pojo.entity.User;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 公共接口
 *
 * @Author chuan-wxy
 * @Create 2024/8/14 20:25
 */
@FeignClient(name = "woj-user-service",path = "/api/user/inner")
public interface UserFeignClient {

    @GetMapping("/test")
    String test();

    @GetMapping("/get/by/id")
    User getById(@RequestParam("id") String id);

    @GetMapping("/get/one/by/account")
    User getByAccount(@RequestParam("account") String account);

    default BaseResponse<User> getLoginUser(HttpServletRequest request) throws StatusFailException {
        String token = request.getHeader("Authorization");

        Claims claimByToken = JwtUtil.getClaimByToken(token);

        String account =(String) claimByToken.get("userAccount");

        User user = this.getByAccount(account);

        if(user == null) {
            throw new StatusFailException("账号认证错误");
        }

        return ResultUtils.success(user);
    };
}
