package com.ddang.walk.controller.request;

import com.ddang.walk.service.request.CompleteWalkServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "산책 완료 Request DTO")
public record CompleteWalkRequest(

        @Schema(description = "산책 총 거리 (미터)", example = "1200")
        @NotNull(message = "산책 총 거리가 존재해야 합니다.")
        @Positive(message = "산책 총 거리는 0보다 커야 합니다.")
        Integer totalDistanceMeter,

        @Schema(description = "산책 총 시간 (초)", example = "1800")
        @NotNull(message = "산책 총 시간이 존재해야 합니다.")
        @Positive(message = "산책 총 시간은 0보다 커야 합니다.")
        Long totalWalkTimeSecond
) {
    public CompleteWalkServiceRequest toServiceRequest() {
        return new CompleteWalkServiceRequest(totalDistanceMeter, totalWalkTimeSecond);
    }
}

