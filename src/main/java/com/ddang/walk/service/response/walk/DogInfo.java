package com.ddang.walk.service.response.walk;

import com.ddang.dog.entity.Dog;
import com.ddang.global.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import static com.ddang.walk.util.DateCalculator.calculateAgeFromNow;

public record DogInfo(
        @Schema(description = "강아지의 식별자", example = "1")
        Long dogId,

        @Schema(description = "강아지 이름", example = "몽이")
        String dogName,

        @Schema(description = "강아지 품종", example = "골든 리트리버")
        String breed,

        @Schema(description = "강아지 프로필 이미지 URL", example = "https://example.com/dog/profile.jpg")
        String dogProfileImg,

        @Schema(description = "강아지 성별", example = "MALE")
        Gender dogGender,

        @Schema(description = "강아지 나이", example = "5")
        long dogAge,

        @Schema(description = "산책 횟수", example = "1")
        int walkCount
) {
    public static DogInfo from(Dog dog){
        return new DogInfo(dog.getDogId(), dog.getName(), dog.getBreed(), dog.getProfileImg(), dog.getGender(),
                calculateAgeFromNow(dog.getBirthDate()), dog.getWalkCount());
    }
}
