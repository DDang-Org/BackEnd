package com.ddang.walk.controller.request;

import com.ddang.walk.service.request.DecisionWalkServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record DecisionWalkRequest(
        @NotNull(message = "상대 이메일은 입력해주셔야 해요")
        @Email(message = "올바른 이메일 형식을 입력해주세요.")
        @Schema(description = "상대방 이메일", example = "example@exaple.com")
        String otherEmail,
        @Schema(description = "수락 거절 여부", example = "ACCEPT")
        String decision
) {
    public DecisionWalkServiceRequest toService(){
        return new DecisionWalkServiceRequest(otherEmail, decision);
    }
}
