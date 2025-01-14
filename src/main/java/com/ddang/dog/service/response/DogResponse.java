package com.ddang.dog.service.response;


import com.ddang.dog.entity.Dog;
import com.ddang.dog.entity.IsNeutered;
import com.ddang.global.entity.Gender;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DogResponse(
        Long dogId,
        String dogName,
        String breed,
        LocalDate dogBirthDate,
        BigDecimal dogWeight,
        Gender dogGender,
        String dogProfileImg,
        IsNeutered isNeutered,
        Integer walkCount,
        Long familyId,
        String comment
) {
    public static DogResponse from(Dog dog){
        return new DogResponse(dog.getDogId(), dog.getName(), dog.getBreed(),
                dog.getBirthDate(), dog.getWeight(), dog.getGender(),
                dog.getProfileImg(), dog.getIsNeutered(), dog.getWalkCount(),
                dog.getFamily().getFamilyId(), dog.getComment());
    }

}
