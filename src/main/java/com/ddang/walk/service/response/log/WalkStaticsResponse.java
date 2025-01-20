package com.ddang.walk.service.response.log;

import com.ddang.walk.service.response.TimeDuration;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "산책 통계 응답")
public record WalkStaticsResponse(

        TimeDuration timeDuration,

        @Schema(description = "산책 횟수", example = "5")
        int walkCount,

        @Schema(description = "총 거리(킬로미터)", example = "10")
        int totalDistanceKilo
) {
    public static WalkStaticsResponse of(long totalSeconds, int walkCount, int totalDistanceKilo) {
        return new WalkStaticsResponse(TimeDuration.from(totalSeconds), walkCount, totalDistanceKilo);
    }
}

