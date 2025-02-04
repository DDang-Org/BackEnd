package com.ddang.notification.controller.request;

import com.ddang.notification.entity.IsAgreed;
import com.ddang.notification.entity.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "알림 설정 요청 데이터")
public record NotificationSettingsRequest(

        @NotNull(message = "알림 타입은 필수입니다.")
        @Schema(description = "알림 타입", example = "WALK")
        Type type,

        @NotNull(message = "알림 설정 여부는 필수입니다.")
        @Schema(description = "알림 설정 여부", example = "TRUE")
        IsAgreed isAgreed
) {
}
