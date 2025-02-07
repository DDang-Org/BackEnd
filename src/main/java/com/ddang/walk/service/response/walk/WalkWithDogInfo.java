package com.ddang.walk.service.response.walk;

import com.ddang.dog.entity.Dog;
import com.ddang.global.entity.Gender;
import com.ddang.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

import static com.ddang.walk.util.DateCalculator.calculateAgeFromNow;


public record WalkWithDogInfo(

        @Schema(description = "상대 강아지 ID", example = "1")
        Long dogId,

        @Schema(description = "상대 강아지 프로필", example = "http://~~~.com")
        String dogProfileImg,

        @Schema(description = "상대 강아지 이름", example = "초코")
        String dogName,

        @Schema(description = "상대 강아지 종", example = "시고르브 잡종")
        String breed,

        @Schema(description = "상대 강아지 나이", example = "3")
        long dogAge,

        @Schema(description = "상대 강아지 성별", example = "MALE")
        Gender dogGender,

        @Schema(description = "회원 ID", example = "1")
        Long memberId
) {
        public static WalkWithDogInfo of(Member member, Dog dog){
                return new WalkWithDogInfo(dog.getDogId(), dog.getProfileImg(), dog.getName(), dog.getBreed(),
                        calculateAgeFromNow(dog.getBirthDate()), dog.getGender(), member.getMemberId());
        }

}
