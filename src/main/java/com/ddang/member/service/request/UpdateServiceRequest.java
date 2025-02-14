package com.ddang.member.service.request;

import com.ddang.global.entity.Gender;
import com.ddang.member.entity.FamilyRole;
import com.ddang.member.entity.Member;

import java.time.LocalDate;

public record UpdateServiceRequest(
        String memberName,
        Gender memberGender,
        LocalDate memberBirthDate,
        String address,
        FamilyRole familyRole,
        int memberProfileImg
) {
    public void toEntity(Member member) {
        member.updateName(memberName);
        member.updateGender(memberGender);
        member.updateBirthDate(memberBirthDate);
        member.updateAddress(address);
        member.updateFamilyRole(familyRole);
        member.updateProfileImg(memberProfileImg);
    }
}
