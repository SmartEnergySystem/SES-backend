package com.SES.config;

import com.SES.dto.deviceMonitor.DeviceDataRedisDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, DeviceDataRedisDTO> deviceDataRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, DeviceDataRedisDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());

        // 创建 ObjectMapper 并注册 Java 8 时间模块
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 构建带自定义 ObjectMapper 的 Redis 序列化器
        Jackson2JsonRedisSerializer<DeviceDataRedisDTO> serializer =
                new Jackson2JsonRedisSerializer<>(DeviceDataRedisDTO.class);

        // 设置序列化器的 ObjectMapper 属性
        serializer.setObjectMapper(mapper);

        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        return template;
    }
}