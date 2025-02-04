package com.ddang.member.controller.request;

import com.ddang.member.entity.IsMatched;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "강번따 허용 여부 수정 요청 데이터")
public record IsMatchedRequest (

    @NotNull(message = "강번따 허용 여부를 입력해주세요.")
    @Schema(description = "강번따 허용 여부", example = "TRUE")
    String isMatched
) {
}
