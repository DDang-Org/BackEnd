package com.ddang.member.service.response;

import com.ddang.dog.entity.Dog;
import com.ddang.dog.entity.IsNeutered;
import com.ddang.global.entity.Gender;
import com.ddang.member.entity.FamilyRole;
import com.ddang.member.entity.Member;
import com.ddang.walk.util.DateCalculator;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record FriendResponse(
        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "회원 이름", example = "John Doe")
        String memberName,

        @Schema(description = "회원 주소", example = "123 Main Street")
        String address,

        @Schema(description = "회원 성별", example = "MALE")
        Gender memberGender,

        @Schema(description = "가족 내 역할", example = "FATHER")
        FamilyRole familyRole,

        @Schema(description = "회원 프로필 이미지 URL", example = "https://example.com/profile.jpg")
        int memberProfileImg,

        @Schema(description = "총 산책 거리 (킬로미터)", example = "12.5")
        double totalDistance,

        @Schema(description = "총 산책 횟수", example = "5")
        int walkCount,

        @Schema(description = "강번따 횟수", example = "3")
        int countWalksWithMember,

        @Schema(description = "강아지 ID", example = "101")
        Long dogId,

        @Schema(description = "강아지 이름", example = "Rex")
        String dogName,

        @Schema(description = "강아지 품종", example = "Golden Retriever")
        String breed,

        @Schema(description = "강아지 나이", example = "5")
        long age,

        @Schema(description = "강아지 무게 (킬로그램)", example = "30")
        BigDecimal weight,

        @Schema(description = "강아지 성별", example = "MALE")
        Gender dogGender,

        @Schema(description = "강아지 프로필 이미지 URL", example = "https://example.com/dog_profile.jpg")
        String dogProfileImg,

        @Schema(description = "중성화 여부", example = "YES")
        IsNeutered isNeutered

) {
    public static FriendResponse of(Member member, Dog dog, double totalDistance, int walkCount, int countWalksWithMember) {
        return new FriendResponse(
                member.getMemberId(),
                member.getName(),
                member.getAddress(),
                member.getGender(),
                member.getFamilyRole(),
                member.getProfileImg(),
                totalDistance,
                walkCount,
                countWalksWithMember,
                dog.getDogId(),
                dog.getName(),
                dog.getBreed(),
                DateCalculator.calculateAgeFromNow(dog.getBirthDate()),
                dog.getWeight(),
                dog.getGender(),
                dog.getProfileImg(),
                dog.getIsNeutered()
        );
    }
}
