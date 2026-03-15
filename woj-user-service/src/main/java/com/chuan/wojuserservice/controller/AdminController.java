package com.chuan.wojuserservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojmodel.pojo.dto.user.UserSearchDTO;
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
     * 根据uuid删除用户
     * @param id
     * @return
     */
    @GetMapping("/delete-by-uuid")
    public BaseResponse<Void> deleteUserByid(@RequestParam(value = "id")String id) throws StatusFailException {
        return adminService.deleteUserByid(id);
    }

    // 查找用户
    @PostMapping("/user-list")
    public BaseResponse<Page<UserAdminVO>> getUserList(@RequestBody UserSearchDTO userSearchDTO,
                                                       @RequestParam("current") Integer current,
                                                       @RequestParam("size") Integer size) throws StatusFailException {
        return adminService.getUserList(userSearchDTO, current, size);
    }

}
