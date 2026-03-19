package com.chuan.wojuserservice;

import com.chuan.wojmodel.pojo.entity.User;
import com.chuan.wojuserservice.mapper.RoleMapper;
import com.chuan.wojuserservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WojUserServiceApplicationTests {
	@Autowired
	UserService userService;

	@Autowired
	RoleMapper roleMapper;

	@Test
	void contextLoads() {
		User user = new User();
		System.out.println(user.getId());

		user.setAccount("12345678");
		user.setPassword("12345678");

		userService.save(user);

		System.out.println(user.getId());
	}

	@Test
	void test() {
		roleMapper.SelectRoleByUserAccount("123123123");
	}

}
