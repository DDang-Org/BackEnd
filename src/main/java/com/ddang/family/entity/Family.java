package com.ddang.family.entity;

import com.ddang.global.entity.BaseEntity;
import com.ddang.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Family extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long familyId;

    @Column(nullable = false)
    private Long representativeMemberId;

    public static Family create() {
        return new Family();
    }

    public void updateRepresentative(Member newRepresentative) {
        this.representativeMemberId = newRepresentative.getMemberId();
    }

}
// TODO : 나중에 더 추가할 예정