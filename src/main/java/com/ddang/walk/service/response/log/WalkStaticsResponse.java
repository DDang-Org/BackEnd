package com.ddang.walk.service.response.log;

import com.ddang.walk.service.response.TimeDuration;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "산책 통계 응답")
public record WalkStaticsResponse(

        TimeDuration timeDuration,

        @Schema(description = "산책 횟수", example = "5")
        int walkCount,

        @Schema(description = "총 거리(미터)", example = "1000")
        int totalDistanceMeter
) {
    public static WalkStaticsResponse of(long totalSeconds, int walkCount, int totalDistanceMeter) {
        return new WalkStaticsResponse(TimeDuration.from(totalSeconds), walkCount, totalDistanceMeter);
    }
}

