package com.ddang.family.service.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record InviteCodeResponse(
        Long familyId,
        String inviteCode,
        long expiresInSeconds
) {
    public static InviteCodeResponse of(Long familyId, String inviteCode, long expiresInSeconds) {
        return new InviteCodeResponse(familyId, inviteCode, expiresInSeconds);
    }
}