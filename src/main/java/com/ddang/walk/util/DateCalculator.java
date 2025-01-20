package com.ddang.walk.util;


import com.ddang.walk.service.response.TimeDuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateCalculator {

    private DateCalculator() {}

    public static long calculateAgeFromNow(LocalDate birthDate){
        return ChronoUnit.YEARS.between(birthDate, LocalDate.now()) + 1;
    }

    public static TimeDuration fromTotalSeconds(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long remainingSeconds = totalSeconds % 3600;
        long minutes = remainingSeconds / 60;
        long seconds = remainingSeconds % 60;
        return new TimeDuration(hours, minutes, seconds);
    }

    public static TimeDuration ofStartTimeToEndTime(LocalDateTime startTime, LocalDateTime endTime) {
        return fromTotalSeconds(ChronoUnit.SECONDS.between(startTime, endTime));
    }
}
