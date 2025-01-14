package com.ddang.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class CookieService {

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    public ResponseCookie createRefreshTokenToCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .path("/")
                .maxAge(refreshTokenExpirationPeriod / 1000)
                .httpOnly(true)
                .sameSite("None") // 필요 시 설정
                .secure(true)     // HTTPS 사용 시 설정
                .build();
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = createRefreshTokenToCookie(refreshToken);
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public Optional<String> getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }
}
