package com.ddang.member.service.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "내 산책 정보 응답 데이터")
public record WalkInfoResponse (

    @Schema(description = "총 산책 거리 (킬로미터)", example = "12.5")
    double totalDistance,

    @Schema(description = "총 산책 횟수", example = "5")
    int walkCount,

    @Schema(description = "강번따 횟수", example = "3")
    int countWalksWithMember
) {
    public static WalkInfoResponse from(double totalDistance, int walkCount, int countWalksWithMember) {
        return new WalkInfoResponse(
                totalDistance,
                walkCount,
                countWalksWithMember
        );
    }
}
