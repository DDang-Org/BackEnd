package com.ddang.walk.service.response.walk;

import com.ddang.dog.entity.Dog;
import com.ddang.dog.service.response.DogResponse;
import com.ddang.global.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import static com.ddang.walk.util.DateCalculator.calculateAgeFromNow;

@Schema(description = "근처 회원 정보 응답 객체")
public record MemberNearbyResponse(
        @Schema(description = "강아지의 식별자", example = "4")
        Long dogId,

        @Schema(description = "강아지 프로필 이미지 URL", example = "https://example.com/dog/profile.jpg")
        String dogProfileImg,

        @Schema(description = "강아지 이름", example = "초코")
        String dogName,

        @Schema(description = "강아지 품종", example = "말티즈")
        String breed,

        @Schema(description = "강아지와의 산책 횟수", example = "10")
        int dogWalkCount,

        @Schema(description = "강아지 나이", example = "5")
        long dogAge,

        @Schema(description = "강아지 성별", example = "FEMALE")
        Gender dogGender,

        @Schema(description = "회원 이메일", example = "example@example.com")
        String email,

        @Schema(description = "메시지 타입", example = "WALK_ALONE")
        Type type
) {
    public static MemberNearbyResponse of(DogResponse dog, String email){
        return new MemberNearbyResponse(dog.dogId(), dog.dogProfileImg(), dog.dogName(), dog.breed(), dog.walkCount(),
                calculateAgeFromNow(dog.dogBirthDate()), dog.dogGender(), email, Type.WALK_ALONE);
    }
}
