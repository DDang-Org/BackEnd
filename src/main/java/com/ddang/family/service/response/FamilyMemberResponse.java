package com.ddang.family.service.response;

import com.ddang.global.entity.Gender;
import com.ddang.member.entity.FamilyRole;
import com.ddang.member.entity.Member;
import com.ddang.member.entity.Provider;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record FamilyMemberResponse(
        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "회원 이름", example = "홍길동")
        String memberName,

        @Schema(description = "회원 이메일", example = "test@naver.com")
        String email,

        @Schema(description = "OAuth2 제공자", example = "NAVER")
        Provider provider,

        @Schema(description = "회원 성별", example = "MALE")
        Gender memberGender,

        @Schema(description = "생일", example = "2000-01-01")
        LocalDate memberBirthDate,

        @Schema(description = "회원 주소", example = "서울시 강남구")
        String address,

        @Schema(description = "가족 내 역할", example = "FATHER")
        FamilyRole familyRole,

        @Schema(description = "회원 프로필 이미지 URL", example = "https://example.com/profile.jpg")
        String memberProfileImg,

        @Schema(description = "패밀리댕 대표 여부", example = "false")
        boolean isRepresent
) {
    public static FamilyMemberResponse of(Member member, boolean represent) {
        return new FamilyMemberResponse(
                member.getMemberId(),
                member.getMemberName(),
                member.getEmail(),
                member.getProvider(),
                member.getMemberGender(),
                member.getMemberBirthDate(),
                member.getAddress(),
                member.getFamilyRole(),
                member.getMemberProfileImg(),
                represent
        );
    }
}
