package com.ddang.notification.service.response;

import com.ddang.notification.entity.IsAgreed;
import com.ddang.notification.entity.Type;
import com.ddang.notification.entity.NotificationSettings;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 설정 업데이트 응답 데이터")
public record SettingsUpdateResponse(

        @Schema(description = "알림 설정 ID", example = "1")
        Long notificationSettingsId,

        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "알림 타입", example = "WALK")
        Type type,

        @Schema(description = "알림 설정 여부", example = "TRUE")
        IsAgreed isAgreed
) {
    public static SettingsUpdateResponse from(NotificationSettings notificationSettings) {
        return new SettingsUpdateResponse(
                notificationSettings.getNotificationSettingId(),
                notificationSettings.getMember().getMemberId(),
                notificationSettings.getType(),
                notificationSettings.getIsAgreed()
        );
    }
}
