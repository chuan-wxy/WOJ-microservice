package com.chuan.wojmodel.pojo.dto.user;

import lombok.Data;

/**
 * @Author: chuan-wxy
 * @Date: 2024/8/17 19:03
 * @Description:
 */
@Data
public class UserLogoutDTO {

    private String userAccount;

    /**
     * 退出时存放jwt
     * 注销时存放注销验证码
     */
    private String code;
}
