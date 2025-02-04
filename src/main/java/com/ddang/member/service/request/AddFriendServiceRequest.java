package com.ddang.member.service.request;

public record AddFriendServiceRequest(
        Long memberId,
        String decision
) {
}
