package com.ddang.walk.repository;

import com.ddang.member.entity.Member;
import com.ddang.walk.entity.Walk;
import com.ddang.walk.entity.WalkDog;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface WalkDogRepository extends JpaRepository<WalkDog, Long> {

   @Query("""
    SELECT wd
    FROM WalkDog wd
    JOIN FETCH wd.walk
    WHERE wd.dog.dogId = :dogId
    AND wd.dog.isDeleted = 'FALSE'
""")
    List<WalkDog> findAllByDog_DogId(@Param("dogId") Long dogId);

    @Query("""
    SELECT wd 
    FROM WalkDog wd 
    WHERE wd.createdAt
    BETWEEN :startYear AND :now
    AND wd.dog.dogId = :dogId""")
    List<WalkDog> findWalkDogsByYearAndDogId(@Param("dogId") Long dogId,
                                             @Param("startYear") LocalDateTime startYear,
                                             @Param("now") LocalDateTime now);

    @Query("""
    SELECT wd.walk
    FROM WalkDog wd
    WHERE wd.dog.dogId = :dogId
""")
    List<Walk> findWalksByDogId(@Param("dogId") Long dogId);

    @Query("""
    SELECT wd.walk
    FROM WalkDog wd
    WHERE wd.dog.dogId = :dogId
    And wd.createdAt
    BETWEEN :startMonth AND :now
""")
    List<Walk> findWalksByDogIdAndMonth(@Param("dogId") Long dogId,
                                        @Param("startMonth") LocalDateTime startMonth,
                                        @Param("now") LocalDateTime now);

    @Query("""
    SELECT wd.walk 
    FROM WalkDog wd 
    JOIN FETCH wd.walk.member 
    WHERE wd.dog.dogId = :dogId
    AND wd.createdAt
    BETWEEN :startYear AND :now
""")
    List<Walk> findWalksByDogIdAndYear(@Param("dogId") Long dogId,
                                @Param("startYear") LocalDateTime startYear,
                                @Param("now") LocalDateTime now);

 @EntityGraph(attributePaths = {"walk"})
 @Query("""
    SELECT wd.walk 
    FROM WalkDog wd 
    JOIN wd.walk w 
    WHERE wd.dog.dogId = :dogId 
    AND w.createdAt 
    BETWEEN :startOfDay AND :endOfDay
""")
 List<Walk> findTodayWalksByDogId(@Param("dogId") Long dogId,
                                     @Param("startOfDay") LocalDateTime startOfDay,
                                     @Param("endOfDay") LocalDateTime endOfDay);

 @Query(value = """
        SELECT wd.walk
        FROM WalkDog wd 
        JOIN FETCH wd.walk.member 
        WHERE wd.walk.member IN :members
        AND wd.dog.dogId = :dogId 
        AND DATE(wd.walk.startTime) = :date
       """)
 List<Walk> findAllByMembersAndDateAndDogId(@Param("members") List<Member> members,
                                    @Param("date") LocalDate date,
                                    @Param("dogId") Long dogId);

}
