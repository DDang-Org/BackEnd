package com.ddang.member.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "AccessToken 재발급 요청 데이터")
public record ReissueRequest(

    @NotNull(message = "회원 이메일을 입력해주세요.")
    @Schema(description = "회원 이메일", example = "email")
    String email
) {
}
