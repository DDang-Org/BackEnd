package com.ddang.member.service.response;

import com.ddang.member.entity.IsMatched;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강번따 허용 여부 응답 데이터")
public record IsMatchedResponse(
        @Schema(description = "강번따 허용 여부", example = "TRUE")
        IsMatched isMatched
) {
    public static IsMatchedResponse from(IsMatched isMatched) {
        return new IsMatchedResponse(isMatched);
    }
}