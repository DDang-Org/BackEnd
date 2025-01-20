package com.ddang.family.service;

import com.ddang.dog.service.response.DogResponse;
import com.ddang.family.service.response.FamilyResponse;
import com.ddang.family.service.response.InviteCodeResponse;
import com.ddang.member.entity.Member;

import java.util.List;

public interface FamilyService {
    InviteCodeResponse createInviteCode(Member member);

    FamilyResponse addMemberToFamily(String inviteCode, Member member);

    List<DogResponse> getFamilyDogs(String inviteCode);
}
