package com.ddang.family.service;

import com.ddang.family.service.response.FamilyResponse;
import com.ddang.family.service.response.InviteCodeResponse;
import com.ddang.member.entity.Member;

public interface FamilyService {
    InviteCodeResponse createInviteCode(Member member);

    FamilyResponse addMemberToFamily(String inviteCode, Member member);
}
