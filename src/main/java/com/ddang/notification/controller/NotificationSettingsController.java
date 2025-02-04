package com.ddang.notification.controller;

import com.ddang.global.api.ApiResponse;
import com.ddang.global.exception.annotation.SwaggerExceptionResponse;
import com.ddang.member.oauth2.CustomOAuth2User;
import com.ddang.notification.controller.request.NotificationSettingsRequest;
import com.ddang.notification.service.NotificationSettingsService;
import com.ddang.notification.service.response.SettingsResponse;
import com.ddang.notification.service.response.SettingsUpdateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.ddang.global.exception.ErrorCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification-settings")
@Tag(name = "Notification Settings API", description = "알림 설정 API")
public class NotificationSettingsController {

    private final NotificationSettingsService notificationSettingsService;

    @GetMapping
    @Operation(summary = "알림 설정 조회", description = "알림 설정 조회 API")
    @SwaggerExceptionResponse({MEMBER_NOT_FOUND, NOTIFICATION_SETTINGS_NOT_FOUND})
    public ApiResponse<SettingsResponse> getSettings(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        return ApiResponse.ok(notificationSettingsService.getSettings(customOAuth2User.getMember().getMemberId()));
    }

    @PatchMapping("/update")
    @Operation(summary = "알림 설정 수정", description = "알림 설정 수정 API")
    @SwaggerExceptionResponse({MEMBER_NOT_FOUND, NOTIFICATION_SETTINGS_NOT_FOUND, INVALID_NOTIFICATION_TYPE, INVALID_IS_AGREED})
    public ApiResponse<SettingsUpdateResponse> updateSettings(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                                              @RequestBody @Valid NotificationSettingsRequest notificationSettingsRequest) {
        return ApiResponse.ok(notificationSettingsService.updateSettings(customOAuth2User.getMember().getMemberId(), notificationSettingsRequest));
    }
}
