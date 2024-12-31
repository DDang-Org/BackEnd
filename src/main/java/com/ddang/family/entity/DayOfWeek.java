package com.ddang.family.entity;

import com.ddang.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DayOfWeek extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dayOfWeekId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WeekDay weekDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "walk_schedule_id",nullable = false)
    private WalkSchedule walkSchedule;

    @Builder
    private DayOfWeek(WeekDay weekDay, WalkSchedule walkSchedule) {
        this.weekDay = weekDay;
        this.walkSchedule = walkSchedule;
    }
}
