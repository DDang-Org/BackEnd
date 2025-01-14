package com.ddang.member.service;

import com.ddang.member.service.request.JoinServiceRequest;
import com.ddang.member.service.response.MemberResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface MemberService {

    MemberResponse join(JoinServiceRequest serviceRequest, HttpServletResponse response);

    String reissueAccessToken(HttpServletRequest request, HttpServletResponse response);

    String logout(HttpServletRequest request);
}
