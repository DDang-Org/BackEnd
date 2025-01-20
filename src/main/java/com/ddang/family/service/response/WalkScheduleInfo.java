package com.ddang.family.service.response;

import com.ddang.family.entity.WeekDay;
import io.swagger.v3.oas.annotations.media.Schema;


import java.time.LocalTime;
import java.util.List;

public record WalkScheduleInfo(
        @Schema(description = "산책 일정 ID", example = "1")
        Long walkScheduleId,

        @Schema(description = "산책 요일 리스트", example = "MONDAY")
        List<WeekDay> weekDayList,

        @Schema(description = "산책 시간", example = "10:00")
        LocalTime walkTime
) {
}
