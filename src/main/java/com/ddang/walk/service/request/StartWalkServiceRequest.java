package com.ddang.walk.service.request;

import java.util.List;

public record StartWalkServiceRequest(
        List<Long> dogIds
) {
}
