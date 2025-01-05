package com.chuan.wojserviceclient.service;

import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.utils.JwtUtil;
import com.chuan.wojcommon.utils.ResultUtils;
import com.chuan.wojmodel.pojo.entity.User;
import com.chuan.wojmodel.pojo.vo.user.UserLoginVO;
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

    @GetMapping("/get/by/useraccount")
    User getByAccount(@RequestParam("userAccount") String userAccount);

    @GetMapping("/get/one/by/useraccount")
    User getOneByUserAccount(@RequestParam("userAccount") String userAccount);

    default BaseResponse<UserLoginVO> getLoginUser(HttpServletRequest request) throws StatusFailException {

        String token = request.getHeader("Authorization");

        Claims claimByToken = JwtUtil.getClaimByToken(token);

        String userAccount =(String) claimByToken.get("userAccount");

        if (userAccount == null || userAccount.isBlank() ){
            throw new StatusFailException("未登录");
        }

        User user = this.getByAccount(userAccount);

        if(user==null || user.getUserAccount().isBlank()){
            throw new StatusFailException("未登录");
        }

        UserLoginVO userLoginVO = new UserLoginVO();

        BeanUtils.copyProperties(user,userLoginVO);

        return ResultUtils.success(userLoginVO);
    };
}
