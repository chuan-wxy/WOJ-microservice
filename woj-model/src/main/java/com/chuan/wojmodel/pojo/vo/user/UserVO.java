package com.chuan.wojmodel.pojo.vo.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * UserVO
 *
 * @Author chuan-wxy
 * @Create 2024/8/14 18:10
 */
@Data
public class UserVO implements Serializable {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 角色列表
     */
    private List<String> roles;

    /**
     * 用户简介
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
    private Data createTime;

    /**
     * iserialVersionUID
     */
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
