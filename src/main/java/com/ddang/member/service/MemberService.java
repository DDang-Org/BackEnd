package com.ddang.member.service;

import com.ddang.member.controller.request.IsMatchedRequest;
import com.ddang.member.service.request.JoinServiceRequest;
import com.ddang.member.service.request.UpdateServiceRequest;
import com.ddang.member.service.response.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface MemberService {

    MemberResponse join(JoinServiceRequest serviceRequest, HttpServletResponse response);

    String reissueAccessToken(HttpServletRequest request, HttpServletResponse response);

    String logout(HttpServletRequest request);

    MyPageResponse getMemberInfo(Long memberId);

    WalkInfoResponse getMemberWalkInfo(Long memberId);

    IsMatchedResponse updateIsMatched(Long memberId, IsMatchedRequest isMatchedRequest);

    UpdateResponse getUpdateInfo(Long memberId);

    UpdateResponse updateMember(Long memberId, UpdateServiceRequest serviceRequest);
}
