package com.chuan.wojmodel.pojo.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录DTO
 *
 * @Author: chuan-wxy
 * @Date: 2024/8/16 15:09
 * @Description:
 */
@Data
public class UserLoginDTO implements Serializable {
    private String userAccount;

    private String userPassword;

}
