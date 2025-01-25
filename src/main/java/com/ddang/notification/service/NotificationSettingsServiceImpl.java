package com.ddang.notification.service;

import com.ddang.global.exception.ErrorCode;
import com.ddang.global.exception.MemberException;
import com.ddang.global.exception.NotificationException;
import com.ddang.member.entity.IsMatched;
import com.ddang.member.entity.Member;
import com.ddang.member.repository.MemberRepository;
import com.ddang.notification.controller.request.NotificationSettingsRequest;
import com.ddang.notification.entity.IsAgreed;
import com.ddang.notification.entity.NotificationSettings;
import com.ddang.notification.entity.Type;
import com.ddang.notification.repository.NotificationSettingsRepository;
import com.ddang.notification.service.response.SettingsResponse;
import com.ddang.notification.service.response.SettingsUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationSettingsServiceImpl implements NotificationSettingsService {

    private final NotificationSettingsRepository notificationSettingsRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public SettingsResponse getSettings(Long memberId) {
        Member member = findMemberById(memberId);

        IsMatched isMatched = member.getIsMatched();

        NotificationSettings chatSettings = findSettingsOrThrow(member, Type.CHAT);
        NotificationSettings friendSettings = findSettingsOrThrow(member, Type.FRIEND);
        NotificationSettings walkSettings = findSettingsOrThrow(member, Type.WALK);

        return SettingsResponse.from(memberId, isMatched, chatSettings, friendSettings, walkSettings);
    }

    @Override
    public SettingsUpdateResponse updateSettings(Long memberId, NotificationSettingsRequest notificationSettingsRequest) {

        Member member = findMemberById(memberId);

        NotificationSettings notificationSettings = findSettingsOrThrow(member, notificationSettingsRequest.type());

        notificationSettings.updateIsAgreed(notificationSettingsRequest.isAgreed());

        return SettingsUpdateResponse.from(notificationSettings);
    }

    @Override
    public void saveDefaultNotificationSettings(Member member) {
        NotificationSettings walkSettings = NotificationSettings.builder()
                .type(Type.WALK)
                .isAgreed(IsAgreed.TRUE)
                .member(member)
                .build();

        NotificationSettings chatSettings = NotificationSettings.builder()
                .type(Type.CHAT)
                .isAgreed(IsAgreed.TRUE)
                .member(member)
                .build();

        NotificationSettings friendSettings = NotificationSettings.builder()
                .type(Type.FRIEND)
                .isAgreed(IsAgreed.TRUE)
                .member(member)
                .build();

        notificationSettingsRepository.save(walkSettings);
        notificationSettingsRepository.save(chatSettings);
        notificationSettingsRepository.save(friendSettings);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private NotificationSettings findSettingsOrThrow(Member member, Type type) {
        return notificationSettingsRepository.findByMemberAndType(member, type)
                .orElseThrow(() -> new NotificationException(ErrorCode.NOTIFICATION_SETTINGS_NOT_FOUND));
    }
}
