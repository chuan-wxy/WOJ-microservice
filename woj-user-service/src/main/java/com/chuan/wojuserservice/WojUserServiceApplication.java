package com.chuan.wojuserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.chuan.wojserviceclient.service"})
@MapperScan("com.chuan.wojuserservice.mapper")
@ComponentScan("com.chuan")
public class WojUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WojUserServiceApplication.class, args);
	}

}
