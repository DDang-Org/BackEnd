package com.ddang.family.entity;

import com.ddang.dog.entity.Dog;
import com.ddang.global.entity.BaseEntity;
import com.ddang.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalkSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walkScheduleId;

    @Column(nullable = false)
    private LocalTime walkTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

    @Builder
    private WalkSchedule(Member member, LocalTime walkTime) {
        this.member = member;
        this.walkTime = walkTime;
    }
}
