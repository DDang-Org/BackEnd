package com.ddang.member.service.request;

import com.ddang.member.entity.*;
import com.ddang.global.entity.Gender;

import java.time.LocalDate;

public record JoinServiceRequest(
        String email,
        Provider provider,
        String memberName,
        Gender memberGender,
        LocalDate memberBirthDate,
        String address,
        FamilyRole familyRole,
        int memberProfileImg,
        IsMatched isMatched,
        Role role
) {
    public Member toEntity() {
        return Member.builder()
                .email(email)
                .provider(provider)
                .name(memberName)
                .gender(memberGender)
                .birthDate(memberBirthDate)
                .address(address)
                .familyRole(familyRole)
                .profileImg(memberProfileImg)
                .isMatched(isMatched)
                .provider(provider)
                .role(role)
                .build();
    }
}
