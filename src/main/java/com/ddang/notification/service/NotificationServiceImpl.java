package com.ddang.notification.service;

import com.ddang.global.exception.ErrorCode;
import com.ddang.global.exception.MemberException;
import com.ddang.member.entity.Member;
import com.ddang.member.repository.MemberRepository;
import com.ddang.notification.entity.Notification;
import com.ddang.notification.repository.NotificationRepository;
import com.ddang.notification.service.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public Slice<NotificationResponse> getNotificationList(Long memberId, Pageable pageable) {
        Member member = findMemberById(memberId);

        Slice<Notification> notifications = notificationRepository.findAllByMember(member, pageable);

        return notifications.map(NotificationResponse::of);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
