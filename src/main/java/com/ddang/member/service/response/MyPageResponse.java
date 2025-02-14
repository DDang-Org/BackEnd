package com.ddang.member.service.response;

import com.ddang.member.entity.FamilyRole;
import com.ddang.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import com.ddang.global.entity.Gender;

import java.time.LocalDate;

@Schema(description = "마이페이지 응답 데이터")
public record MyPageResponse(
        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "회원 이름", example = "홍길동")
        String memberName,

        @Schema(description = "회원 이메일", example = "test@naver.com")
        String email,

        @Schema(description = "회원 주소", example = "서울시 강남구")
        String address,

        @Schema(description = "회원 성별", example = "MALE")
        Gender memberGender,

        @Schema(description = "회원 생년월일", example = "1990-01-01")
        LocalDate memberBirthDate,

        @Schema(description = "가족 내 역할", example = "FATHER")
        FamilyRole familyRole,

        @Schema(description = "회원 프로필 이미지 URL", example = "https://example.com/profile.jpg")
        int memberProfileImg,

        @Schema(description = "패밀리댕 대표 여부", example = "true")
        boolean isRepresentative
) {
    public static MyPageResponse from(Member member) {
        boolean isRepresentative = member.getFamily() != null
                && member.getFamily().getRepresentativeMemberId().equals(member.getMemberId());
        return new MyPageResponse(
                member.getMemberId(),
                member.getName(),
                member.getEmail(),
                member.getAddress(),
                member.getGender(),
                member.getBirthDate(),
                member.getFamilyRole(),
                member.getProfileImg(),
                isRepresentative
        );
    }
}
