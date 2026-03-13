package com.chuan.wojmodel.pojo.vo.user;

import com.chuan.wojmodel.pojo.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * @author chuan_wxy
 *
 */
@Data
public class UserAdminVO extends UserVO implements Serializable {

    private Integer isDelete;

    public static UserAdminVO objToVo(User user) {
        UserAdminVO userAdminVO = new UserAdminVO();
        BeanUtils.copyProperties(user, userAdminVO);
        return userAdminVO;
    }

}
