package com.ddang.notification.repository;

import com.ddang.member.entity.Member;
import com.ddang.notification.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Slice<Notification> findAllByMember(Member member, Pageable pageable);
}