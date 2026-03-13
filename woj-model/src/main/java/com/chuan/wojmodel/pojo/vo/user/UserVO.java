package com.chuan.wojmodel.pojo.vo.user;

import com.baomidou.mybatisplus.annotation.*;
import com.chuan.wojmodel.pojo.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
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
    private String id;

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

    /**
     * iserialVersionUID
     */
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public static UserVO objToVo(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

}
