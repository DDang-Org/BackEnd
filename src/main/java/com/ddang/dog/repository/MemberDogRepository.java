package com.ddang.dog.repository;

import com.ddang.dog.entity.MemberDog;
import com.ddang.member.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberDogRepository extends JpaRepository<MemberDog, Long> {

    @EntityGraph(attributePaths = {"dog"})
    @Query("SELECT md FROM MemberDog md WHERE md.dog.dogId = :dogId AND md.member.memberId = :memberId AND md.isDeleted = 'FALSE'")
    Optional<MemberDog> findByDogIdAndMemberId(Long dogId, Long memberId);

    @Modifying
    @Query("UPDATE MemberDog md SET md.isDeleted = 'TRUE' WHERE md.dog.dogId = :dogId")
    void softDeleteByDogId(@Param("dogId") Long dogId);


    @EntityGraph(attributePaths = {"dog"})
    @Query("SELECT md FROM MemberDog md WHERE md.member = :member AND md.isDeleted = 'FALSE'")
    List<MemberDog> findAllByMember(@Param("member") Member member);

    @Query("SELECT count(*) FROM MemberDog md WHERE md.member = :member AND md.isDeleted = 'FALSE'")
    Integer countAllByMember(Member member);

}
