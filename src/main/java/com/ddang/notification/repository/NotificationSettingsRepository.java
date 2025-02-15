package com.ddang.notification.repository;

import com.ddang.member.entity.Member;
import com.ddang.notification.entity.NotificationSettings;
import com.ddang.notification.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {

    Optional<NotificationSettings> findByMemberAndType(Member member, Type type);

    @Modifying
    @Query("""
            UPDATE NotificationSettings ns
            SET ns.isDeleted = 'TRUE'
            WHERE ns.member.memberId = :memberId
            """)
    void deleteByMember(Long memberId);
}
