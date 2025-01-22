package com.ddang.walk.repository;

import com.ddang.member.entity.Member;
import com.ddang.walk.entity.Walk;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface WalkRepository extends JpaRepository<Walk, Long> {

    @EntityGraph(attributePaths = {"member"})
    @Query(value = """
        SELECT w
        FROM Walk w 
        WHERE w.member IN :members 
        AND DATE(w.startTime) = :date
       """)
    List<Walk> findAllByMembersAndDate(@Param("members") List<Member> members, @Param("date") LocalDate date);


}
