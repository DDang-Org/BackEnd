package com.ddang.family.service.response;

import com.ddang.family.entity.Family;
import io.swagger.v3.oas.annotations.media.Schema;

public record FamilyResponse(
        @Schema(description = "가족 ID", example = "1")
        Long familyId,

        @Schema(description = "가족 대표자 회원 ID", example = "42")
        Long memberId
) {
    public static FamilyResponse from(Family family) {
        return new FamilyResponse(family.getFamilyId(), family.getRepresentativeMemberId());
    }
}