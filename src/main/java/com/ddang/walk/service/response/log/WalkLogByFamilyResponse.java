package com.ddang.walk.service.response.log;

import com.ddang.member.entity.FamilyRole;
import com.ddang.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "가족 구성원의 산책 기록 응답")
public record WalkLogByFamilyResponse(
        @Schema(description = "멤버 ID", example = "1")
        Long memberId,

        @Schema(description = "가족 역할", example = "FATHER")
        FamilyRole familyRole,

        @Schema(description = "이름", example = "춘식이")
        String memberName,

        @Schema(description = "산책 횟수", example = "10")
        int count
) {
        public static WalkLogByFamilyResponse of(Member member, int count){
                return new WalkLogByFamilyResponse(member.getMemberId(), member.getFamilyRole(), member.getMemberName(), count);

        }
}
