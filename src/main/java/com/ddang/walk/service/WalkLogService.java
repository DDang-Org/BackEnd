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

    List<Integer> getYearlyWalkLog(Member member);

    List<WalkLogByFamilyResponse> getYearlyWalkLogByFamily(Member member);

    WalkStaticsResponse getTotalWalkLog(Member member);

    WalkStaticsResponse getMonthlyTotalWalk(Member member);
}
