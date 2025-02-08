package com.ddang.walk.controller.request;

import com.ddang.walk.service.request.StartWalkServiceRequest;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record StartWalkRequest(
        @NotEmpty(message = "최소 한 마리 이상의 개를 산책시켜야 합니다.")
        List<Long> dogIds
) {
    public StartWalkServiceRequest toService(){
        return new StartWalkServiceRequest(dogIds);
    }
}
