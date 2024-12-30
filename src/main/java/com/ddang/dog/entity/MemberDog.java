package com.ddang.dog.entity;

import com.ddang.global.entity.BaseEntity;
import com.ddang.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberDogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id",nullable = false)
    private Dog dog;

    @Builder
    private MemberDog(Member member, Dog dog) {
        this.member = member;
        this.dog = dog;
    }
}
