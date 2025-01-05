package com.chuan.wojuserservice.service;

import com.chuan.wojcommon.common.BaseResponse;

/**
 * 描述
 *
 * @Author chuan-wxy
 * @Create 2024/8/15 13:55
 */
public interface EmailService {
    BaseResponse<String> getCaptchaCode(String email, String content);
}
