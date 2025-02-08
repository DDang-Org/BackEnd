package com.ddang.walk.service.response.walk;


import com.ddang.walk.service.request.WalkServiceRequest;

public record WalkWithResponse(
        String email,
        double latitude,
        double longitude,
        Type type
) {
    public static WalkWithResponse of(String email, WalkServiceRequest serviceRequest){
        return new WalkWithResponse(email, serviceRequest.latitude(), serviceRequest.longitude(), Type.WALK_WITH);
    }
}
