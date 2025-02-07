package com.ddang.walk.service.request;

import java.time.LocalDateTime;

public record StartWalkServiceRequest(
        double latitude,
        double longitude
)
{
    public String toStringFormat(){
        return String.format("longitude=%f, latitude=%f, timestamp=%s", longitude, latitude, LocalDateTime.now());
    }
}
