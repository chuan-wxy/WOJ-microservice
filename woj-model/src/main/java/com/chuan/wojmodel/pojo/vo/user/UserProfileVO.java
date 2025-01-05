package com.chuan.wojmodel.pojo.vo.user;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

/**
 * @Author: chuan-wxy
 * @Date: 2024/8/20 22:03
 * @Description:
 */
public class UserProfileVO implements Serializable {

    /**
     * 账号（邮箱）
     */
    private String userAccount;

    /**
     * 昵称
     */
    private String userName;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 学校
     */
    private String school;

    /**
     * 专业
     */
    private String course;

    /**
     * 学号
     */
    private String number;

    /**
     * 性别
     */
    private String gender;

    /**
     * github地址
     */
    private String github;

    /**
     * 博客地址
     */
    private String blog;

    /**
     * 个性签名
     */
    private String signature;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
