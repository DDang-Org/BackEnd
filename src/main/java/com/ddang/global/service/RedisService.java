package com.ddang.global.service;

import com.ddang.global.exception.ErrorCode;
import com.ddang.global.exception.RedisException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

import static com.ddang.global.service.RedisKey.POINT_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate redisTemplate;

    public void setValues(String key, String data, Duration duration) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data, duration);
    }

    public void setValues(String key, String data) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data);
    }

    public void setListValues(String key, String value){
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        listOperations.rightPush(key, value);
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

    public GeoResults<RedisGeoCommands.GeoLocation<String>> getNearbyMemberResults(Point memberLocation, int meter, int number){
        Distance radius = new Distance(meter, RedisGeoCommands.DistanceUnit.METERS);
        Circle circle = new Circle(memberLocation, radius);

        GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending()
                .limit(number);

        return geoOperations.radius(POINT_KEY, circle, args);
    }

    public Point getMemberPoint(String key, String id){
        List<Point> position = redisTemplate.opsForGeo().position(key, id);

        if(position == null || position.isEmpty()){
            throw new IllegalArgumentException("위치 정보가 존재하지 않음");
        }

        return position.get(0);
    }

    public void setGeoValues(String key, String id, Point point){
        GeoOperations<String, Object> geoOperations = redisTemplate.opsForGeo();
        geoOperations.add(key, point, id);
    }
}
