package com.ddang.walk.service.response.walk;

import com.ddang.dog.service.response.DogResponse;
import com.ddang.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

public record DecisionWalkResponse(
        @Schema(description = "상대방 동의 여부", example = "ACCEPT")
        String decision,

        @Schema(description = "상대방 강아지 이름", example = "빠삐용")
        String dogName,

        @Schema(description = "상대방 멤버 Id", example = "빠삐용")
        Long memberId,

        @Schema(description = "상대방 프로필 이미지", example = "Avatar5.svg")
        int memberProfileImg,

        @Schema(description = "메시지 타입", example = "DECISION")
        Type type
) {
    public static DecisionWalkResponse of(String decision, Member member, DogResponse dog){
        return new DecisionWalkResponse(decision, dog.dogName(), member.getMemberId() ,member.getProfileImg(), Type.DECISION);
    }
}
