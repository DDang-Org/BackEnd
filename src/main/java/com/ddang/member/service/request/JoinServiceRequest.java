package com.ddang.member.service.request;

import com.ddang.member.entity.*;
import com.ddang.global.entity.Gender;

import java.time.LocalDate;

public record JoinServiceRequest(
        String email,
        Provider provider,
        String name,
        Gender gender,
        LocalDate birthDate,
        String address,
        FamilyRole familyRole,
        String profileImg,
        IsMatched isMatched,
        Role role
) {
    public Member toEntity() {
        return Member.builder()
                .email(email)
                .provider(provider)
                .name(name)
                .gender(gender)
                .birthDate(birthDate)
                .address(address)
                .familyRole(familyRole)
                .profileImg(profileImg)
                .isMatched(isMatched)
                .provider(provider)
                .role(role)
                .build();
    }
}
