package com.ddang.member.service;

import com.ddang.global.exception.AuthenticationException;
import com.ddang.global.exception.ErrorCode;
import com.ddang.global.exception.MemberException;
import com.ddang.member.entity.Member;
import com.ddang.member.jwt.service.JwtService;
import com.ddang.member.repository.MemberRepository;
import com.ddang.member.service.request.JoinServiceRequest;
import com.ddang.member.service.response.MemberResponse;
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
    private final JwtService jwtService;

    @Override
    public MemberResponse join(JoinServiceRequest serviceRequest, HttpServletResponse response) {

        Member member = serviceRequest.toEntity();

        memberRepository.save(member);

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
}
