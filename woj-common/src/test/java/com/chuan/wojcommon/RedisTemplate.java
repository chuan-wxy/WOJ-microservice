package com.chuan.wojcommon;

import com.chuan.wojcommon.utils.RedisUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisTemplate {

    @Resource
    private RedisUtil redisUtil;
    @Test
    public void test() {
        redisUtil.set("key", "value");
        String s = (String) redisUtil.get("key");
        System.out.println(s);
    }
}
