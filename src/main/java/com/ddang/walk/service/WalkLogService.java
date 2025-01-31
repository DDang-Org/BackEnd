package com.ddang.walk.service;


import com.ddang.member.entity.Member;
import com.ddang.walk.service.response.log.WalkLogByFamilyResponse;
import com.ddang.walk.service.response.log.WalkLogResponse;
import com.ddang.walk.service.response.log.WalkStaticsResponse;

import java.time.LocalDate;
import java.util.List;

public interface WalkLogService {
    List<LocalDate> getWalkLogs(Member member, Long dogId);

    List<WalkLogResponse> getWalkLogByDate(Member member, LocalDate date, Long dogId);

    List<Integer> getYearlyWalkLog(Member member, Long dogId);

    List<WalkLogByFamilyResponse> getYearlyWalkLogByFamily(Member member, Long dogId);

    WalkStaticsResponse getTotalWalkLog(Member member, Long dogId);

    WalkStaticsResponse getMonthlyTotalWalk(Member member, Long dogId);
}
