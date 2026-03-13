package com.chuan.wojmodel.pojo.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录DTO
 *
 * @Author: chuan-wxy
 * @Date: 2024/8/16 15:09
 * @Description:
 */
@Data
public class UserLoginDTO {
    @NotBlank(message = "账号不能为空")
    private String userAccount;

    @NotBlank(message = "密码不能为空")
    private String userPassword;
}
