package com.ddang.walk.controller.request;

import com.ddang.walk.service.request.ProposalWalkServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record ProposalWalkRequest(
        @NotNull(message = "이메일을 입력해야합니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Schema(description = "상대방 이메일", example = "example@exaple.com")
        String email,
        @Schema(description = "한마디 코멘트", example = "같이 산책 해요 :)")
        String comment

) {
    public ProposalWalkServiceRequest toService() {
        return new ProposalWalkServiceRequest(email, comment);
    }
}
