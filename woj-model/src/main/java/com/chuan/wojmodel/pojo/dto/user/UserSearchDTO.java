package com.chuan.wojmodel.pojo.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author chuan_wxy
 *
 */
@Data
public class UserSearchDTO implements Serializable {

    private String id;

    private String userAccount;

    private String userName;

    private String school;

    private String number;

    private Integer gender;

    private Integer isDelete;

    @Serial
    private static final long serialVersionUID = 1L;
}
