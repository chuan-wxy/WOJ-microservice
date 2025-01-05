package com.chuan.wojmodel.pojo.vo.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

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
     * 头像url
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Data createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 开放平台id
     */
    private String mpOpenId;

    /**
     * iserialVersionUID
     */
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
