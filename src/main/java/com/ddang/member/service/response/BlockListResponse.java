package com.ddang.member.service.response;

import com.ddang.global.entity.Gender;
import com.ddang.member.entity.Block;
import com.ddang.member.entity.FamilyRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "차단 정보 목록 응답 데이터")
public record BlockListResponse(

        @Schema(description = "차단 ID", example = "1")
        Long blockId,

        @Schema(description = "차단 대상 회원 이름", example = "홍길동")
        String blockedMemberName,

        @Schema(description = "차단 대상 회원 성별", example = "MALE")
        Gender memberGender,

        @Schema(description = "차단 대상 회원 가족 내 역할", example = "FATHER")
        FamilyRole familyRole
) {
    public static BlockListResponse from(Block block) {
        return new BlockListResponse(
                block.getBlockId(),
                block.getBlocked().getName(),
                block.getBlocked().getGender(),
                block.getBlocked().getFamilyRole()
        );
    }
}
