package com.ddang.dog.service.response;

import com.ddang.walk.service.response.TimeDuration;

public record DogWalkResponse(
        TimeDuration timeDuration,
        long totalDistanceMeter,
        int totalCalorie
) {
    public static DogWalkResponse of(TimeDuration timeDuration, long totalDistanceMeter, int totalCalorie){
        return new DogWalkResponse(timeDuration, totalDistanceMeter, totalCalorie);
    }
}
