package com.chuan.wojuserservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojmodel.pojo.entity.User;
import com.chuan.wojmodel.pojo.vo.user.UserVO;

import java.util.List;

/**
 * 特殊接口
 *
 * @Author chuan-wxy
 * @Create 2024/8/18 15:34
 */
public interface AdminService extends IService<User> {
    BaseResponse<UserVO> searchUserById(String uuid);

    BaseResponse<List<UserVO>> searchUserByGneder(String gender);

    BaseResponse<Void> deleteUserByUuid(String uuid);
}
