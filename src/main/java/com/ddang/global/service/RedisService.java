package com.ddang.global.service;

import com.ddang.global.exception.ErrorCode;
import com.ddang.global.exception.RedisException;
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
            throw new RedisException(ErrorCode.REDIS_DATA_DELETE_ERROR);
        }
    }

    public void deleteGeoValues(String key, String id){
        if (redisTemplate.opsForGeo().remove(key, id) != 1) {
            throw new IllegalArgumentException("위치 정보를 삭제하지 못했습니다.");
        }
    }

    public boolean checkHasKey(String key){
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
