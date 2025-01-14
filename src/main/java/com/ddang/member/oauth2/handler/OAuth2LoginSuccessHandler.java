package com.ddang.member.oauth2.handler;

import com.ddang.member.entity.Member;
import com.ddang.member.entity.Role;
import com.ddang.member.jwt.service.JwtService;
import com.ddang.member.oauth2.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        try {
            Member member = ((CustomOAuth2User) authentication.getPrincipal()).getMember();
            String accessToken = jwtService.createAccessToken(member.getEmail(), member.getProvider().name());

            // 최초 로그인인 경우 추가 정보 입력을 위한 회원가입 페이지로 리다이렉트
            if (member.getRole() == Role.GUEST) {
                handleGuestLogin(response, member);
            } else {
                handleUserLogin(response, accessToken, member.getEmail());
            }
        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 오류 발생: ", e);
            throw e;
        }
    }

    private void handleGuestLogin(HttpServletResponse response, Member member) throws IOException {
        log.info("GUEST 상태 - 추가 정보 입력 페이지로 리디렉션");

        // email과 provider 정보를 객체로 묶어서 리디렉션
        String redirectUrl = "https://ddang.pages.dev/register?email=" + member.getEmail() + "&provider=" + member.getProvider().name();
        log.info("Redirecting to: {}", redirectUrl);

        // 리디렉션
        response.sendRedirect(redirectUrl);
    }

    private void handleUserLogin(HttpServletResponse response, String accessToken, String email) throws IOException {
        log.info("USER 상태 - 로그인 성공 처리");

        String refreshToken = jwtService.createRefreshToken(email);
        jwtService.saveRefreshTokenToRedis(email, refreshToken);
        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        // 사용자가 로그인 성공 시 홈 페이지로 리디렉션
        response.sendRedirect("https://ddang.pages.dev/?accessToken=" + accessToken);
    }
}

