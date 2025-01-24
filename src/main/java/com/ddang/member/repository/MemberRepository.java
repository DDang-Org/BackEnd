package com.ddang.member.repository;

import com.ddang.family.entity.Family;
import com.ddang.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m WHERE m.email = :email AND m.isDeleted = 'FALSE'")
    Optional<Member> findByEmail(@Param("email") String email);

    @Query("SELECT m FROM Member m WHERE m.family = :family AND m.isDeleted = 'FALSE'")
    List<Member> findAllByFamily(Family family);

    @Query("SELECT m FROM Member m WHERE m.memberId = :memberId AND m.isDeleted = 'FALSE'")
    Optional<Member> findById(@Param("memberId") Long memberId);
}
