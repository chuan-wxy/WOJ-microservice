package com.chuan.wojmodel.pojo.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册DTO
 *
 * @Author: chuan-wxy
 * @Date: 2024/8/15 17:17
 * @Description:
 */
@Data
public class UserRegisterDTO implements Serializable {

    private String userAccount;

    private String userPassword;

    private String rePassword;

    private String captcha;

    @Serial
    private static final long serialVersionUID = 1L;

}
