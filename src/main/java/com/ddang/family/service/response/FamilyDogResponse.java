package com.ddang.family.service.response;

import com.ddang.dog.entity.Dog;
import com.ddang.dog.entity.IsNeutered;
import com.ddang.global.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FamilyDogResponse(
        Long dogId,
        String dogName,
        String breed,
        LocalDate dogBirthDate,
        BigDecimal weight,
        Gender dogGender,
        String dogProfileImg,
        IsNeutered isNeutered,
        Integer walkCount,
        Long familyId,
        String comment,
        double totalDistanceInKilometers,
        int totalCalorie

) {
    public static FamilyDogResponse of(Dog dog, double totalDistanceInKilometers, int totalCalorie){
        return new FamilyDogResponse(dog.getDogId(), dog.getName(), dog.getBreed(),
                dog.getBirthDate(), dog.getWeight(), dog.getGender(),
                dog.getProfileImg(), dog.getIsNeutered(), dog.getWalkCount(),
                dog.getFamily().getFamilyId(), dog.getComment(), totalDistanceInKilometers, totalCalorie);
    }
}
