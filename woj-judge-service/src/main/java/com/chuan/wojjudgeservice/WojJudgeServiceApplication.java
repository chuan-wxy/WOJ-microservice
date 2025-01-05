package com.chuan.wojjudgeservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.chuan.wojserviceclient.service"})
@ComponentScan("com.chuan")
@MapperScan("com.chuan.wojjudgeservice.mapper")
public class WojJudgeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WojJudgeServiceApplication.class, args);
    }

}
