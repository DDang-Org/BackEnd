package com.ddang.member.service;

import com.ddang.global.exception.AuthenticationException;
import com.ddang.global.exception.ErrorCode;
import com.ddang.global.exception.MemberException;
import com.ddang.member.controller.request.IsMatchedRequest;
import com.ddang.member.entity.IsMatched;
import com.ddang.member.entity.Member;
import com.ddang.member.jwt.service.JwtService;
import com.ddang.member.repository.MemberRepository;
import com.ddang.member.repository.WalkWithMemberRepository;
import com.ddang.member.service.request.JoinServiceRequest;
import com.ddang.member.service.request.UpdateServiceRequest;
import com.ddang.member.service.response.*;
import com.ddang.notification.service.NotificationSettingsService;
import com.ddang.walk.repository.WalkRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final WalkRepository walkRepository;
    private final WalkWithMemberRepository walkWithMemberRepository;
    private final JwtService jwtService;
    private final NotificationSettingsService notificationSettingsService;

    @Override
    public MemberResponse join(JoinServiceRequest serviceRequest, HttpServletResponse response) {

        Member member = serviceRequest.toEntity();

        memberRepository.save(member);

        notificationSettingsService.saveDefaultNotificationSettings(member);

        String accessToken = jwtService.createAccessToken(member.getEmail(), member.getProvider().name());
        String refreshToken = jwtService.createRefreshToken(member.getEmail());

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.saveRefreshTokenToRedis(member.getEmail(), refreshToken);

        return MemberResponse.from(member);
    }

    @Override
    public String reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = jwtService.extractRefreshTokenFromCookie(request)
                .filter(jwtService::isTokenValid)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.UNAUTHORIZED_RTK_ERROR));

        String email = jwtService.extractEmailFromRefreshToken(refreshToken)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.UNAUTHORIZED_RTK_ERROR));

        jwtService.getRefreshTokenFromRedis(email)
                .filter(storedToken -> storedToken.equals(refreshToken))
                .orElseThrow(() -> new AuthenticationException(ErrorCode.UNAUTHORIZED_RTK_ERROR));

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        String newAccessToken = jwtService.createAccessToken(member.getEmail(), member.getProvider().name());
        String newRefreshToken = jwtService.createRefreshToken(member.getEmail());

        jwtService.removeRefreshTokenFromRedis(email); // 기존 토큰 삭제
        jwtService.saveRefreshTokenToRedis(member.getEmail(), newRefreshToken); // 새로운 토큰 저장

        jwtService.sendAccessAndRefreshToken(response, newAccessToken, newRefreshToken);

        return newAccessToken;
    }

    @Override
    public String logout(HttpServletRequest request) {

        String accessToken = jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.UNAUTHORIZED_ATK_ERROR));

        String email = jwtService.extractEmail(accessToken)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.UNAUTHORIZED_ATK_ERROR));

        if (jwtService.getRefreshTokenFromRedis(email).isPresent()) {
            jwtService.removeRefreshTokenFromRedis(email);
        }

        return "Success Logout";
    }


    @Override
    @Transactional(readOnly = true)
    public MyPageResponse getMemberInfo(Long memberId) {
        Member member = findMemberById(memberId);

        return MyPageResponse.from(member);
    }

    @Override
    @Transactional(readOnly = true)
    public WalkInfoResponse getMemberWalkInfo(Long memberId) {
        findMemberById(memberId);

        int totalDistanceInMeters = walkRepository.findTotalDistanceByMemberId(memberId);
        int countWalks = walkRepository.countWalksByMemberId(memberId);

        double totalDistanceInKilometers = totalDistanceInMeters / 1000.0;
        int countWalksWithMember = walkWithMemberRepository.countBySenderMemberId(memberId);

        return WalkInfoResponse.from(totalDistanceInKilometers, countWalks, countWalksWithMember);
    }

    @Override
    public IsMatchedResponse updateIsMatched(Long memberId, IsMatchedRequest isMatchedRequest) {
        Member member = findMemberById(memberId);
        IsMatched isMatched = validateIsMatched(isMatchedRequest.isMatched());

        member.updateIsMatched(isMatched);
        return IsMatchedResponse.from(member.getIsMatched());
    }

    @Override
    @Transactional(readOnly = true)
    public UpdateResponse getUpdateInfo(Long memberId) {
        Member member = findMemberById(memberId);

        return UpdateResponse.from(member);
    }

    @Override
    public UpdateResponse updateMember(Long memberId, UpdateServiceRequest serviceRequest) {
        Member member = findMemberById(memberId);

        serviceRequest.toEntity(member);

        return UpdateResponse.from(member);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private IsMatched validateIsMatched(String isMatchedValue) {
        try {
            return IsMatched.valueOf(isMatchedValue);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new MemberException(ErrorCode.INVALID_IS_MATCHED);
        }
    }
}
