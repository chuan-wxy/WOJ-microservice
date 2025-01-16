package com.chuan.wojuserservice.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.common.ResultStatus;
import com.chuan.wojcommon.common.enums.EmailEnum;
import com.chuan.wojcommon.utils.EmailUtil;
import com.chuan.wojcommon.utils.RedisUtil;
import com.chuan.wojcommon.utils.ResultUtils;
import com.chuan.wojmodel.pojo.entity.User;
import com.chuan.wojuserservice.service.EmailService;
import com.chuan.wojuserservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 描述
 *
 * @Author chuan-wxy
 * @Create 2024/8/15 13:55
 */
@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    UserService userService;

    @Autowired
    RedisUtil redisUtil;

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public BaseResponse<String> getCaptchaCode(String email, String content) {
        // todo 根据网站配置，判断是否开启注册
        boolean isEmail = Validator.isEmail(email);
        if (!isEmail) {
            return new BaseResponse(400, "您的邮箱格式不正确！");
        }

        String lockKey = EmailEnum.REGISTER_EMAIL_LOCK + email;
        if(redisUtil.hasKey(lockKey)) {
            return new BaseResponse(400,"对不起，您的操作频率过快，请在" + redisUtil.getExpire(lockKey) + "秒后再次发送注册邮件！");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("userAccount",email);

        User user = userService.getOne(queryWrapper);
        if (user != null) {
            System.out.println(1);
            return ResultUtils.error("改邮箱已被注册");
        }

        String numbers = RandomUtil.randomNumbers(6);

        redisUtil.set(EmailEnum.REGISTER_KEY_PREFIX.getValue() + email, numbers, 10 * 60);//默认验证码有效10分钟
        redisUtil.set(lockKey, 0, 60);
        log.info(email+"正在发送验证码");
        if(EmailUtil.send(email,numbers,content)) {
            log.info(email+"验证码发送成功");
            return new BaseResponse(ResultStatus.OK);
        } else {
            log.info(email + "验证码发送失败");
            return new BaseResponse(400,"验证码发送失败");
        }
    }
}
