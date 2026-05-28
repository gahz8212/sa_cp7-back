package com.paycoms.cp7.global.auth.config;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @SuppressWarnings("null")
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    private final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // Key-Value를 문자열로 직렬화 (가독성 때문)
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }

    // 블랙리스트에 토큰 저장 (남은 유효시간만큼만 저장)
    @SuppressWarnings("null")
    public void setBlackList(String accessToken, String msg, long duration) {
        redisTemplate.opsForValue().set(accessToken, msg, duration, TimeUnit.MILLISECONDS);
    }

    // 블랙리스트 포함 여부 확인
    @SuppressWarnings("null")
    public boolean hasKeyBlackList(String accessToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(accessToken));
    }
}
