package com.ddang.notification.service;

import com.ddang.notification.service.response.NotificationResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import com.ddang.member.entity.Member;

public interface NotificationService {

    Slice<NotificationResponse> getNotificationList(Long memberId, Pageable pageable);
}
