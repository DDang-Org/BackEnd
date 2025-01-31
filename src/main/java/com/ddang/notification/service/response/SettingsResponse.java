package com.ddang.notification.service.response;

import com.ddang.member.entity.IsMatched;
import com.ddang.notification.entity.IsAgreed;
import com.ddang.notification.entity.NotificationSettings;
import com.ddang.notification.entity.Type;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.LinkedHashMap;
import java.util.Map;

@Schema(description = "알림 설정 응답 데이터")
public record SettingsResponse(

        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "강번따 허용 여부", example = "TRUE")
        IsMatched isMatched,

        @Schema(description = "채팅 알림 허용 여부", example = "TRUE")
        IsAgreed chatNotificationAllowed,

        @Schema(description = "친구 알림 허용 여부", example = "TRUE")
        IsAgreed friendNotificationAllowed,

        @Schema(description = "산책 알림 허용 여부", example = "TRUE")
        IsAgreed walkNotificationAllowed
) {
    public static SettingsResponse from(
            Long memberId,
            IsMatched isMatched,
            NotificationSettings chatSettings,
            NotificationSettings friendSettings,
            NotificationSettings walkSettings
    ) {
        return new SettingsResponse(
                memberId,
                isMatched,
                chatSettings.getIsAgreed(),
                friendSettings.getIsAgreed(),
                walkSettings.getIsAgreed()
        );
    }
}