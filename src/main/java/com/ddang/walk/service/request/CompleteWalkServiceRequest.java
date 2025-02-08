package com.ddang.walk.service.request;


import com.ddang.member.entity.Member;
import com.ddang.walk.entity.Walk;

import java.time.LocalDateTime;

public record CompleteWalkServiceRequest(
        Integer totalDistance,
        Long totalWalkTime
) {

    public Walk toEntity(LocalDateTime startTime , LocalDateTime endTime, Member member, String walkImg){
        return  Walk.builder()
                .totalDistance(totalDistance)
                .member(member)
                .walkImg(walkImg)
                .startTime(startTime)
                .endTime(endTime)
                .build();

    }
}
