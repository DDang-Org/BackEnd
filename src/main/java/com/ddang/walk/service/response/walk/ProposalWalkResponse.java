package com.ddang.walk.service.response.walk;

import com.ddang.dog.service.response.DogResponse;
import com.ddang.global.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import static com.ddang.walk.util.DateCalculator.calculateAgeFromNow;

@Schema(description = "산책 제안 응답 객체")
public record ProposalWalkResponse(
        @Schema(description = "강아지의 식별자", example = "1")
        Long dogId,

        @Schema(description = "강아지 이름", example = "몽이")
        String dogName,

        @Schema(description = "강아지 품종", example = "골든 리트리버")
        String dogBreed,

        @Schema(description = "강아지 프로필 이미지 URL", example = "https://example.com/dog/profile.jpg")
        String dogProfileImg,

        @Schema(description = "추가 코멘트", example = "같이 산책 해요 :)")
        String comment,

        @Schema(description = "강아지 성별", example = "MALE")
        Gender dogGender,

        @Schema(description = "강아지 나이", example = "5")
        long dogAge,

        @Schema(description = "회원 이메일", example = "example@example.com")
        String email,

        @Schema(description = "메시지 타입", example = "PROPOSAL")
        Type type
){
    public static ProposalWalkResponse of(DogResponse dog, String email, String comment){
        return new ProposalWalkResponse(dog.dogId(), dog.dogName(), dog.breed(), dog.dogProfileImg(),
                comment, dog.dogGender(), calculateAgeFromNow(dog.dogBirthDate()), email, Type.PROPOSAL);
    }
}

