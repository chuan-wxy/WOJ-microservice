package com.chuan.wojmodel.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "user")
public class User implements Serializable {
    /**
     * UUID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 微信开放平台id
     */
    private String unionId;

    /**
     * 公众号openId
     */
    private String openId;

    /**
     * 账号（邮箱）
     */
    private String account;

    /**
     * 密码 2-20位
     */
    private String password;

    /**
     * 昵称
     */
    private String name;

    /**
     * 简介
     */
    private String profile;

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
     * 性别（0：保密，1：男，2：女）
     */
    private Integer gender;

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

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @Serial
    private static final long serialVersionUID = 1L;
}