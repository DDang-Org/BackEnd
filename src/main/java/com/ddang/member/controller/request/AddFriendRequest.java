package com.ddang.member.controller.request;

import com.ddang.member.service.request.AddFriendServiceRequest;
import jakarta.validation.constraints.NotNull;

public record AddFriendRequest(
        @NotNull(message = "memberId 는 필수 값 입니다.")
        Long memberId,

        @NotNull(message = "ACCEPT 혹은 DENY 필수 입니다.")
        String decision
) {
    public AddFriendServiceRequest toService(){
        return new AddFriendServiceRequest(memberId, decision);
    }
}
