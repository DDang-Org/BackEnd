package com.ddang.chat.service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatMessageServiceRequest(
        String senderEmail,
        String receiverEmail,
        String message
) {
}
