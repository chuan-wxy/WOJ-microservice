package com.chuan.wojmodel.pojo.dto.user;

import lombok.Data;

/**
 * 用户注册DTO
 *
 * @Author: chuan-wxy
 * @Date: 2024/8/15 17:17
 * @Description:
 */
@Data
public class UserRegisterDTO {

    private String userAccount;

    private String userPassword;

    private String rePassword;

    private String captcha;

}
