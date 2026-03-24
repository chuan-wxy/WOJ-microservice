package com.chuan.wojaiservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author chuan_wxy
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.chuan.wojserviceclient.service"})
@ComponentScan("com.chuan")
@MapperScan("com.chuan.wojaiservice.mapper")
public class WojAiServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WojAiServiceApplication.class, args);
    }

}



