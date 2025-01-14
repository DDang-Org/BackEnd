package com.ddang.member.repository;

import com.ddang.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m WHERE m.email = :email AND m.isDeleted = 'FALSE'")
    Optional<Member> findByEmail(@Param("email") String email);
}
