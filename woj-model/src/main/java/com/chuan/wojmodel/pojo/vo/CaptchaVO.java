package com.chuan.wojmodel.pojo.vo;

import lombok.Data;

/**
 * 验证码VO
 * @Author: chuan-wxy
 * @Date: 2024/8/15 12:14
 * @Description: 返回前端注册验证码的VO类
 */
@Data
public class CaptchaVO {
    private String registerCode;
}
