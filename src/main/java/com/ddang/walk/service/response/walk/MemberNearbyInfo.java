package com.ddang.walk.service.response.walk;


import com.ddang.global.entity.Gender;
import com.ddang.member.entity.IsMatched;

import java.time.LocalDate;

public record MemberNearbyInfo(
        Long dogId,
        String breed,
        String dogName,
        String dogProfileImg,
        int walkCount,
        Long memberId,
        LocalDate dogBirthDate,
        Gender dogGender,
        IsMatched isMatched,
        String email
) { }

