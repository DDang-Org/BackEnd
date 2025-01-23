package com.ddang.member.service.response;

import com.ddang.global.entity.Gender;
import com.ddang.member.entity.FamilyRole;
import com.ddang.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "내 정보 수정 응답 데이터")
public record UpdateResponse(

        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "회원 이름", example = "홍길동")
        String memberName,

        @Schema(description = "회원 성별", example = "MALE")
        Gender memberGender,

        @Schema(description = "회원 주소", example = "서울시 강남구")
        String address,

        @Schema(description = "가족 내 역할", example = "FATHER")
        FamilyRole familyRole,

        @Schema(description = "회원 프로필 이미지 URL", example = "https://example.com/profile.jpg")
        int memberProfileImg
) {
    public static UpdateResponse from(Member member) {
        return new UpdateResponse(
                member.getMemberId(),
                member.getName(),
                member.getGender(),
                member.getAddress(),
                member.getFamilyRole(),
                member.getProfileImg()
        );
    }
}
