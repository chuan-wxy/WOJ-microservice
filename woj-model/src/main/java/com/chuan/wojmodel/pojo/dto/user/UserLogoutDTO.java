package com.chuan.wojmodel.pojo.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: chuan-wxy
 * @Date: 2024/8/17 19:03
 * @Description:
 */
@Data
public class UserLogoutDTO implements Serializable {

    private String userAccount;

    /**
     * 退出时存放jwt
     * 注销时存放注销验证码
     */
    private String code;

    @Serial
    private static final long serialVersionUID = 1L;

}
