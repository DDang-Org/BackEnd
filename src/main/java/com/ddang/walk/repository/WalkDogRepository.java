package com.ddang.walk.repository;

import com.ddang.walk.entity.Walk;
import com.ddang.walk.entity.WalkDog;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
    List<WalkDog> findAllByDog_DogId(Long dogId);

    @Query("""
    SELECT wd 
    FROM WalkDog wd 
    WHERE YEAR(wd.createdAt) = :year 
    AND wd.dog.dogId = :dogId""")
    List<WalkDog> findWalkDogsByYearAndDogId(@Param("year") int year, @Param("dogId") Long dogId);

    @Query("""
    SELECT wd.walk
    FROM WalkDog wd
    WHERE wd.dog.dogId = :dogId
""")
    List<Walk> findWalksByDogIdFromMemberId(Long memberId, Long dogId);

    @Query("""
    SELECT wd.walk
    FROM WalkDog wd
    WHERE wd.dog.dogId = :dogId
    And Month(wd.createdAt) = :month
""")
    List<Walk> findWalksByDogIdAndMonthFromMemberId(Long memberId, Long dogId, int month);

    @Query("""
    SELECT wd.walk 
    FROM WalkDog wd 
    JOIN FETCH wd.walk.member 
    WHERE wd.dog.dogId = :dogId
    AND YEAR(wd.createdAt) = :year
""")
    List<Walk> findWalksByDogId(@Param("dogId") Long dogId, @Param("year") int year);

 @EntityGraph(attributePaths = {"walk"})
 @Query("""
    SELECT wd.walk 
    FROM WalkDog wd 
    JOIN wd.walk w 
    WHERE wd.dog.dogId = :dogId 
    AND w.createdAt 
    BETWEEN :startOfDay AND :endOfDay
""")
 List<Walk> findWalksByDogIdAndToday(@Param("dogId") Long dogId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

}
