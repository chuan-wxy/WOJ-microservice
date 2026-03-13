package com.chuan.wojuserservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojmodel.pojo.vo.user.UserAdminVO;
import com.chuan.wojmodel.pojo.vo.user.UserVO;
import com.chuan.wojuserservice.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员接口
 *
 * @Author: chuan-wxy
 * @Date: 2024/8/18 10:57
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    AdminService adminService;
    /**
     * 根据id查询用户
     * @param uuid
     * @return
     */
    @GetMapping("/search-by-uuid")
    public BaseResponse<UserVO> searchUserById(String uuid) {
        return adminService.searchUserById(uuid);
    }

    /**
     * 根据性别查询用户
     * @param gender
     * @return
     */
    @GetMapping("/search-list-by-gender")
    public BaseResponse<List<UserVO>> searchUserByGender(String gender) {
        return adminService.searchUserByGneder(gender);
    }

    /**
     * 根据uuid删除用户
     * @param uuid
     * @return
     */
    @GetMapping("/delete-by-uuid")
    public BaseResponse<Void> deleteUserByid(@RequestParam(value = "id")String uuid, HttpServletRequest request) {
        return adminService.deleteUserByid(uuid, request);
    }

    // 查找用户
    @GetMapping("/user-list")
    public BaseResponse<Page<UserAdminVO>> getUserList() {
        return adminService.getUserList();
    }

}
