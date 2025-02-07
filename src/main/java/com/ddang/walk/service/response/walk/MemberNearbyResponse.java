package com.ddang.walk.service.response.walk;

import com.ddang.dog.entity.Dog;
import com.ddang.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "근처 회원 정보 응답 객체")
public record MemberNearbyResponse(
       List<DogInfo> dogInfos,

        @Schema(description = "회원의 고유 식별자", example = "5")
        Long memberId,

        @Schema(description = "회원 이메일", example = "example@example.com")
        String email,

        @Schema(description = "메시지 타입", example = "WALK_ALONE")
        Type type
) {
    public static MemberNearbyResponse from(List<Dog> dogs, Member member){
        return new MemberNearbyResponse(dogs.stream().map(DogInfo::from).toList(),
                member.getMemberId(), member.getEmail(), Type.WALK_ALONE);
    }
}
