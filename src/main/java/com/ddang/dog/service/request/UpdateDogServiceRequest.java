package com.ddang.dog.service.request;

import com.ddang.dog.entity.IsNeutered;
import com.ddang.global.entity.Gender;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateDogServiceRequest(
        Long dogId,
        String dogName,
        String dogBreed,
        LocalDate dogBirthDate,
        BigDecimal dogWeight,
        Gender dogGender,
        IsNeutered isNeutered,
        String comment
) {}

