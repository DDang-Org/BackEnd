package com.ddang.walk.repository;

import com.ddang.member.entity.Member;
import com.ddang.walk.entity.Walk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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

    @Query("""
    SELECT w.member.memberId, COUNT(w) 
    FROM Walk w 
    WHERE w.member IN :members 
    GROUP BY w.member.memberId
    """)
    List<Object[]> countWalksByMembers(@Param("members") List<Member> members);
}
