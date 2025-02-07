package com.ddang.walk.service.request;

public record ProposalWalkServiceRequest(
        String otherMemberEmail,
        String comment
) {
}
