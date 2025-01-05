package com.chuan.wojcommon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.chuan")
@ComponentScan("com.chuan")
public class WojCommonApplication {

	public static void main(String[] args) {
		SpringApplication.run(WojCommonApplication.class, args);
	}

}
