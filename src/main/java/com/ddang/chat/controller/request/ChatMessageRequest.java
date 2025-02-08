package com.ddang.chat.controller.request;

import com.ddang.chat.service.request.ChatMessageServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatMessageRequest(
        @Schema(description = "수신자 email", example = "example@example.com")
        @NotBlank(message = "수신자 email은 필수입니다.")
        String receiverEmail,
        @Schema(description = "전송할 채팅 메시지", example = "안녕하세요!")
        @NotBlank(message = "채팅 메세지는 필수입니다.")
        String message
) {
    public ChatMessageServiceRequest toServiceRequest(String senderEmail) {
        return new ChatMessageServiceRequest(senderEmail, receiverEmail, message);
    }
}
