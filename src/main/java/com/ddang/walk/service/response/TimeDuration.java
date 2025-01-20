package com.ddang.walk.service.response;

import com.ddang.walk.util.DateCalculator;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "시간 기간을 나타내는 클래스")
public record TimeDuration(

        @Schema(description = "시간 수", example = "1")
        long hours,
        @Schema(description = "분 수", example = "30")
        long minutes,
        @Schema(description = "초 수", example = "45")
        long seconds
) {

    public static TimeDuration from(long totalSeconds) {
        return DateCalculator.fromTotalSeconds(totalSeconds);
    }

    public static TimeDuration of(LocalDateTime startTime, LocalDateTime endTime) {
        return DateCalculator.ofStartTimeToEndTime(startTime, endTime);
    }
}
