package com.chuan.wojuserservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chuan.wojmodel.pojo.dto.role.UserRoleDTO;
import com.chuan.wojmodel.pojo.dto.user.UserSearchDTO;
import com.chuan.wojmodel.pojo.entity.User;
import com.chuan.wojmodel.pojo.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import org.apache.ibatis.annotations.Param;


/**
 * 描述
 *
 * @Author chuan-wxy
 * @Create 2024/8/14 20:27
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    IPage<User> selectUserListWithDeleted(Page<User> page, @Param("query") UserSearchDTO query);

    List<UserRoleDTO> selectRoleNamesByUids(@Param("uids") List<Long> uids);
}
