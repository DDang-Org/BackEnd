package com.ddang.member.service.response;

import com.ddang.global.entity.Gender;
import com.ddang.member.entity.FamilyRole;
import com.ddang.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

public record FriendListResponse(

        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "회원 성별", example = "MALE")
        Gender memberGender,

        @Schema(description = "가족 내 역할", example = "FATHER")
        FamilyRole familyRole,

        @Schema(description = "프로필 이미지", example = "http://asdasdasd.asdad")
        int memberProfileImg,

        @Schema(description = "회원 이름", example = "춘식이")
        String memberName
) {
    public static FriendListResponse from(Member member){
        return new FriendListResponse(member.getMemberId(), member.getGender(), member.getFamilyRole(), member.getProfileImg(), member.getName());
    }
}
