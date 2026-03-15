package com.chuan.wojcommon.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import java.math.BigInteger;


/**
 * @author chuan_wxy
 *
 */
@AutoConfiguration
public class JacksonConfig {

    /**
     * Jackson 全局配置：将 Long 类型序列化为 String
     * 解决前端 JavaScript 处理 Long 类型（超过 17 位）时的精度丢失问题
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            // 将 Long 类型（以及其包装类）和 BigInteger 全部序列化为 String
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(Long.TYPE, ToStringSerializer.instance);
            builder.serializerByType(BigInteger.class, ToStringSerializer.instance);
        };
    }
}