package com.ddang.notification.controller;

import com.ddang.global.api.ApiResponse;
import com.ddang.global.exception.BadRequestException;
import com.ddang.global.exception.annotation.SwaggerExceptionResponse;
import com.ddang.member.oauth2.CustomOAuth2User;
import com.ddang.notification.service.NotificationService;
import com.ddang.notification.service.response.NotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ddang.global.exception.ErrorCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
@Tag(name = "Notification API", description = "알림 API")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/list")
    @Operation(
            summary = "알림 목록 조회",
            description = "로그인한 사용자의 알림 목록을 최신순으로 10개씩 페이징하여 반환합니다.",
            parameters = {
                    @Parameter(name = "page", description = "조회할 페이지 번호 (기본값: 0)", required = false)
            }
    )
    @SwaggerExceptionResponse({MEMBER_NOT_FOUND, INVALID_PAGE_NUMBER})
    public ApiResponse<Slice<NotificationResponse>> getNotificationList(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @RequestParam(defaultValue = "0") int page // 기본값은 첫 페이지
    ) {
        if (page < 0) {
            throw new BadRequestException(INVALID_PAGE_NUMBER);
        }
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("createdAt").descending());  // 최신순 정렬
        Slice<NotificationResponse> notificationList = notificationService.getNotificationList(customOAuth2User.getMember().getMemberId(), pageRequest);
        return ApiResponse.ok(notificationList);
    }
}
