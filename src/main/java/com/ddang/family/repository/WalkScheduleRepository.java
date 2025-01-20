package com.ddang.family.repository;

import com.ddang.family.entity.WalkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WalkScheduleRepository extends JpaRepository<WalkSchedule, Long> {
    @Query("""
    SELECT ws 
    FROM WalkSchedule ws 
    WHERE ws.member.memberId = :memberId
    """)
    List<WalkSchedule> findByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("""
    DELETE FROM WalkSchedule ws
    WHERE ws.member.memberId = :memberId
    """)
    void deleteByMemberId(@Param("memberId") Long memberId);
}