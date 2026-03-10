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

    private UserVO userInfo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
