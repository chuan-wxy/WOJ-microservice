package com.chuan.wojmodel.pojo.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author chuan_wxy
 *
 */
@Data
public class UserPasswordDTO implements Serializable {
    @NotBlank(message = "旧密码不能为空")
    private String password;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, max = 20, message = "新密码长度必须在 8-20 位之间")
    private String newPassword;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @Serial
    private static final long serialVersionUID = 1L;
}
