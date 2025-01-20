package com.ddang.family.repository;

import com.ddang.family.entity.DayOfWeek;
import com.ddang.family.entity.WalkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DayOfWeekRepository extends JpaRepository<DayOfWeek, Long> {

    @Query("""
    SELECT ws.member.memberId, ws.walkScheduleId, GROUP_CONCAT(dow.weekDay), ws.walkTime
    FROM WalkSchedule ws
    JOIN DayOfWeek dow ON ws.walkScheduleId = dow.walkSchedule.walkScheduleId
    WHERE ws.member.memberId IN :memberIds
    GROUP BY ws.walkScheduleId
    """)
    List<Object[]> findSchedulesWithDaysByMemberIds(@Param("memberIds") List<Long> memberIds);

    @Modifying
    @Query("""
    DELETE FROM DayOfWeek d 
    WHERE d.walkSchedule = :walkSchedule
    """)
    void deleteByWalkSchedule(@Param("walkSchedule") WalkSchedule walkSchedule);
}
