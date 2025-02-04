package com.ddang.walk.repository;

import com.ddang.walk.entity.Walk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WalkRepository extends JpaRepository<Walk, Long> {

    @Query("""
            SELECT COALESCE(SUM(w.totalDistance), 0)
            FROM Walk w
            WHERE w.member.memberId = :memberId AND w.isDeleted = 'FALSE'
            """)
    int findTotalDistanceByMemberId(@Param("memberId") Long memberId);

    @Query("""
            SELECT COUNT(w)
            FROM Walk w
            WHERE w.member.memberId = :memberId AND w.isDeleted = 'FALSE'
            """)
    int countWalksByMemberId(@Param("memberId") Long memberId);
}
