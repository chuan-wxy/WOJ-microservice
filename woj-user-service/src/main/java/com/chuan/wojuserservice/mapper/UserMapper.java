package com.chuan.wojuserservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chuan.wojmodel.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;


/**
 * 描述
 *
 * @Author chuan-wxy
 * @Create 2024/8/14 20:27
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
