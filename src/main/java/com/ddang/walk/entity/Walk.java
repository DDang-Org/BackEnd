package com.ddang.walk.entity;

import com.ddang.global.entity.BaseEntity;
import com.ddang.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Walk extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walkId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private int totalDistance;

    @Column(nullable = false)
    private int totalCalorie;

    private String walkImg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

    @Builder
    private Walk(LocalDateTime startTime, LocalDateTime endTime, int totalDistance, String walkImg, Member member) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalDistance = totalDistance;
        this.walkImg = walkImg;
        this.member = member;
    }
}
