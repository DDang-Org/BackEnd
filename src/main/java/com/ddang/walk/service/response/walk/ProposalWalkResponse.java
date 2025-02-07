package com.ddang.walk.service.response.walk;

import com.ddang.dog.entity.Dog;
import com.ddang.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "산책 제안 응답 객체")
public record ProposalWalkResponse(
        List<DogInfo> dogs,

        @Schema(description = "추가 코멘트", example = "같이 산책 해요 :)")
        String comment,

        @Schema(description = "회원 이메일", example = "example@example.com")
        String email,

        @Schema(description = "메시지 타입", example = "PROPOSAL")
        Type type
){
    public static ProposalWalkResponse of(List<Dog> dogs, Member member, String comment){

        return new ProposalWalkResponse(dogs.stream().map(DogInfo::from).toList(), comment,
                member.getEmail(), Type.PROPOSAL);
    }
}
