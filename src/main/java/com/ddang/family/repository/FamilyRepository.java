package com.ddang.family.repository;

import com.ddang.family.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FamilyRepository extends JpaRepository<Family, Long> {

    @Query("""
        SELECT f 
        FROM Family f 
        WHERE f.familyId = :id 
          AND f.isDeleted = 'FALSE'
    """)
    Optional<Family> findActiveById(@Param("id") Long id);
}
