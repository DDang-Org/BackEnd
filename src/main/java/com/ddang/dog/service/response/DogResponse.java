package com.ddang.dog.service.response;


import com.ddang.dog.entity.Dog;
import com.ddang.dog.entity.IsNeutered;
import com.ddang.global.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DogResponse(
        @Schema(description = "강아지 ID", example = "1")
        Long dogId,

        @Schema(description = "강아지 이름", example = "바둑이")
        String dogName,

        @Schema(description = "견종", example = "골든 리트리버")
        String breed,

        @Schema(description = "강아지 생년월일", example = "2020-05-10")
        LocalDate dogBirthDate,

        @Schema(description = "몸무게 (kg 단위)", example = "12.5")
        BigDecimal weight,

        @Schema(description = "강아지 성별", example = "MALE")
        Gender dogGender,

        @Schema(description = "강아지 프로필 이미지 URL", example = "https://example.com/profile.jpg")
        String dogProfileImg,

        @Schema(description = "중성화 여부", example = "NEUTERED")
        IsNeutered isNeutered,

        @Schema(description = "산책 횟수", example = "15")
        Integer walkCount,

        @Schema(description = "가족 ID", example = "100")
        Long familyId,

        @Schema(description = "추가 설명", example = "산책을 좋아하는 강아지")
        String comment

) {
    public static DogResponse from(Dog dog){
        return new DogResponse(dog.getDogId(), dog.getName(), dog.getBreed(),
                dog.getBirthDate(), dog.getWeight(), dog.getGender(),
                dog.getProfileImg(), dog.getIsNeutered(), dog.getWalkCount(),
                dog.getFamily().getFamilyId(), dog.getComment());
    }

}
