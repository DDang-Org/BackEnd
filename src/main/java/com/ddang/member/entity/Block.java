package com.ddang.member.entity;

import com.ddang.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Block extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", nullable = false)
    private Member blocker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", nullable = false)
    private Member blocked;

    @Builder
    private Block(Member blocker, Member blocked) {
        this.blocker = blocker;
        this.blocked = blocked;
    }

    public static Block of(Member blocker, Member blocked) {
        return Block.builder()
                .blocker(blocker)
                .blocked(blocked)
                .build();
    }
}
