package com.chuan.wojuserservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chuan.wojmodel.pojo.dto.role.UserRoleDTO;
import com.chuan.wojmodel.pojo.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author chuan-wxy
* @description 针对表【userrole】的数据库操作Mapper
* @createDate 2024-08-20 12:22:33
* @Entity generator.domain.Userrole
*/
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    List<UserRoleDTO> selectRoleNamesByUids(@Param("uids") List<Long> uids);

}




