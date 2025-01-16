package com.chuan.wojuserservice.controller.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chuan.wojmodel.pojo.entity.User;
import com.chuan.wojserviceclient.service.UserFeignClient;
import com.chuan.wojuserservice.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {

    @Resource
    private UserService userService;

    @Override
    @GetMapping("/test")
    public String test(){
        return "usertest";
    }

    @Override
    @GetMapping("/get/by/id")
    public User getById(@RequestParam("id") String uuid){
        return userService.getById(uuid);
    }

    @Override
    @GetMapping("/get/by/useraccount")
    public User getByAccount(String userAccount) {
        return userService.getUserByAccount(userAccount);
    }

    @Override
    @GetMapping("/get/one/by/useraccount")
    public User getOneByUserAccount(@RequestParam("userAccount") String userAccount) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        return userService.getOne(queryWrapper);
    }
}
