package com.ddang.global.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate redisTemplate;

    public void setValues(String key, String data, Duration duration) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data, duration);
    }

    public String getValues(String key) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        if (values.get(key) == null) {
            return null;
        }
        return (String) values.get(key);
    }

    public void deleteValues(String key) {
        if(Boolean.FALSE.equals(redisTemplate.delete(key))){
            throw new IllegalArgumentException("Redis 에서 정보를 삭제하지 못했습니다.");
        }
    }
}
