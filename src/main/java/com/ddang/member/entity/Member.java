package com.ddang.member.entity;

import com.ddang.family.entity.Family;
import com.ddang.global.entity.BaseEntity;
import com.ddang.global.entity.Gender;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, length = 100)
    private String memberName;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private LocalDate memberBirthDate;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String memberProfileImg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender memberGender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FamilyRole familyRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IsMatched isMatched;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    public Member(String memberName, String email, LocalDate memberBirthDate, String address, String memberProfileImg, Gender memberGender, FamilyRole familyRole, IsMatched isMatched, Family family, Provider provider, Role role) {
        this.memberName = memberName;
        this.email = email;
        this.memberBirthDate = memberBirthDate;
        this.address = address;
        this.memberProfileImg = memberProfileImg;
        this.memberGender = memberGender;
        this.familyRole = familyRole;
        this.isMatched = isMatched;
        this.family = family;
        this.provider = provider;
        this.role = role;
    }
}
