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
    private String name;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private int profileImg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate birthDate;

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
    public Member(Long memberId, String name, String email, String address, int profileImg, Gender gender, LocalDate birthDate, FamilyRole familyRole, IsMatched isMatched, Family family, Provider provider, Role role) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.address = address;
        this.profileImg = profileImg;
        this.gender = gender;
        this.birthDate = birthDate;
        this.familyRole = familyRole;
        this.isMatched = isMatched;
        this.family = family;
        this.provider = provider;
        this.role = role;
    }

    public void updateFamily(Family family){
        this.family = family;
    }

    public boolean hasNoFamily(){
        return this.family == null;
    }

    public void updateIsMatched(IsMatched isMatched) {
        this.isMatched = isMatched;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateGender(Gender gender) {
        this.gender = gender;
    }

    public void updateAddress(String address) {
        this.address = address;
    }

    public void updateFamilyRole(FamilyRole familyRole) {
        this.familyRole = familyRole;
    }

    public void updateProfileImg(int profileImg) {
        this.profileImg = profileImg;
    }
}
