package com.chuan.wojmodel.pojo.vo.user;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * UserLoginVO
 *
 * @Author chuan-wxy
 * @Create 2024/8/14 18:15
 */
@Data
public class UserLoginVO implements Serializable{

    private String jwt;

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
     * 头像地址
     */
    private String avatar;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 头衔、称号
     */
    private String titleName;

    /**
     * 头衔、称号的颜色
     */
    private String titleColor;

    /**
     * 创建时间
     */
    private Date createTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
