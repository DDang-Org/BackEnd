package com.ddang.notification.service;

import com.ddang.member.entity.Member;
import com.ddang.notification.controller.request.NotificationSettingsRequest;
import com.ddang.notification.service.response.SettingsResponse;
import com.ddang.notification.service.response.SettingsUpdateResponse;
import jakarta.validation.Valid;

public interface NotificationSettingsService {

    SettingsResponse getSettings(Long memberId);

    SettingsUpdateResponse updateSettings(Long memberId, NotificationSettingsRequest notificationSettingsRequest);

    void saveDefaultNotificationSettings(Member member);
}
