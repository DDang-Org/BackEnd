package com.ddang.notification.repository;

import com.ddang.member.entity.Member;
import com.ddang.notification.entity.NotificationSettings;
import com.ddang.notification.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {

    Optional<NotificationSettings> findByMemberAndType(Member member, Type type);
}
