package com.ddang.dog.service.request;


import com.ddang.dog.entity.Dog;
import com.ddang.dog.entity.IsNeutered;
import com.ddang.family.entity.Family;
import com.ddang.global.entity.Gender;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateDogServiceRequest(
        String dogName,
        String breed,
        LocalDate dogBirthDate,
        BigDecimal weight,
        Gender dogGender,
        IsNeutered isNeutered,
        String comment
) {
    public Dog toEntity(String profileImg, Family family){

        return Dog.builder()
                .name(dogName)
                .breed(breed)
                .birthDate(dogBirthDate)
                .weight(weight)
                .gender(dogGender)
                .isNeutered(isNeutered)
                .profileImg(profileImg)
                .family(family)
                .comment(comment)
                .build();

    }
}

