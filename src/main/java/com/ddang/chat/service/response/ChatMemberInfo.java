package com.ddang.chat.service.response;

import com.ddang.global.entity.Gender;
import com.ddang.member.entity.FamilyRole;
import com.ddang.member.entity.Member;

public record ChatMemberInfo(
        Long memberId,
        String memberName,
        String email,
        Gender memberGender,
        FamilyRole familyRole,
        int memberProfileImg
) {

    public static ChatMemberInfo from(Member member) {
        return new ChatMemberInfo(
                member.getMemberId(),
                member.getName(),
                member.getEmail(),
                member.getGender(),
                member.getFamilyRole(),
                member.getProfileImg()
        );
    }

    public static ChatMemberInfo fromKafka(com.ddang.chat.service.request.ChatMessageKafkaRequest request) {
        return new ChatMemberInfo(
                request.sendMemberId(),
                request.sendMemberName(),
                request.sendEmail(),
                request.sendMemberGender(),
                request.sendFamilyRole(),
                request.sendMemberProfileImg()
        );
    }
}
