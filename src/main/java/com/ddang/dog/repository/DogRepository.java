package com.ddang.dog.repository;

import com.ddang.dog.entity.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DogRepository extends JpaRepository<Dog, Long> {
  
    @Modifying
    @Query("UPDATE Dog d SET d.isDeleted = 'TRUE' WHERE d.dogId = :dogId")
    void softDeleteById(@Param("dogId") Long dogId);
  
    @Query("SELECT d FROM Dog d WHERE d.dogId = :id AND d.isDeleted = 'FALSE'")
    Optional<Dog> findActiveById(@Param("id") Long id);
}
