package com.ddang.dog.service.response;


import com.ddang.dog.entity.IsNeutered;
import com.ddang.global.entity.Gender;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateDogResponse(
        Long dogId,
        String dogName,
        String dogBreed,
        LocalDate dogBirthDate,
        BigDecimal dogWeight,
        Gender dogGender,
        String dogProfileImg,
        IsNeutered isNeutered,
        Long familyId,
        String comment
) {}
