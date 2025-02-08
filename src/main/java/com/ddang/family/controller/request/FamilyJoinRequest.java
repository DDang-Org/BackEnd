package com.ddang.family.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "가족 참여 요청 데이터")
public record FamilyJoinRequest(
        @NotBlank(message = "초대 코드는 필수입니다.")
        @Schema(description = "가족 초대 코드", example = "ABC12345")
        String inviteCode
) {
}