package com.ddang.member.repository;

import com.ddang.member.entity.WalkWithMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WalkWithMemberRepository extends JpaRepository<WalkWithMember, Long> {

    @Query("""
        SELECT COUNT(w)
        FROM WalkWithMember w
        WHERE w.sender.memberId = :memberId AND w.isDeleted = 'FALSE'
        """)
    int countBySenderMemberId(@Param("memberId") Long memberId);

}
