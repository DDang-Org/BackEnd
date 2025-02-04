package com.ddang.member.service.response;

import com.ddang.member.entity.Block;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "차단 정보 응답 데이터")
public record BlockResponse(

        @Schema(description = "차단 ID", example = "1")
        Long blockId,

        @Schema(description = "차단자 회원 ID", example = "1")
        Long blockerMemberId,

        @Schema(description = "차단 대상 회원 ID", example = "2")
        Long blockedMemberId,

        @Schema(description = "차단 대상 회원 이름", example = "홍길동")
        String blockedMemberName
) {
    public static BlockResponse from(Block block) {
        return new BlockResponse(
                block.getBlockId(),
                block.getBlocker().getMemberId(),
                block.getBlocked().getMemberId(),
                block.getBlocked().getName()
        );
    }
}
