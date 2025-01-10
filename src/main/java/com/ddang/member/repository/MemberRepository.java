package com.ddang.member.repository;

import com.ddang.family.entity.Family;
import com.ddang.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findAllByFamily(Family family);
}
