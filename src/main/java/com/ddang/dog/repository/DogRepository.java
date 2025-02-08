package com.ddang.dog.repository;

import com.ddang.dog.entity.Dog;
import com.ddang.family.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DogRepository extends JpaRepository<Dog, Long> {
  
    @Modifying
    @Query("UPDATE Dog d SET d.isDeleted = 'TRUE' WHERE d.dogId = :dogId")
    void softDeleteById(@Param("dogId") Long dogId);
  
    @Query("SELECT d FROM Dog d WHERE d.dogId = :id AND d.isDeleted = 'FALSE'")
    Optional<Dog> findActiveById(@Param("id") Long id);

    @Query("SELECT d FROM Dog d WHERE d.family = :family AND d.isDeleted = 'FALSE'")
    List<Dog> findDogsByFamily(@Param("family") Family family);

    @Query("SELECT d FROM Dog d WHERE d.dogId IN :dogIds AND d.isDeleted = 'FALSE'")
    List<Dog> findDogsByDogIds(@Param("dogIds") List<Long> dogIds);
}
