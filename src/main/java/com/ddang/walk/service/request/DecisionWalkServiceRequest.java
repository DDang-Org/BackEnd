package com.ddang.walk.service.request;

public record DecisionWalkServiceRequest(
        String otherEmail,
        String decision
) {
}
