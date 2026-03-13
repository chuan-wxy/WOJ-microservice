package com.chuan.wojcommon;

import com.chuan.wojcommon.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisTemplate {

    @Resource
    private RedisUtils redisUtils;
    @Test
    public void test() {
        redisUtils.set("key", "value");
        String s = (String) redisUtils.get("key");
        System.out.println(s);
    }
}
