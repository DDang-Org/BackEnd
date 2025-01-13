package com.ddang.member.jwt.filter;

import com.ddang.global.api.ApiResponse;
import com.ddang.member.entity.Member;
import com.ddang.member.jwt.service.JwtService;
import com.ddang.member.oauth2.CustomOAuth2User;
import com.ddang.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final List<String> EXCLUDED_URLS = Arrays.asList(
            "/login", "/api/v1/member/join", "/api/v1/member/sign-up", "/favicon.ico",
            "/api/v1/member/reissue", "/swagger", "/swagger-ui.html",
            "/swagger-ui/index.html", "/swagger-ui", "/v3/api-docs", "/ws", "/default-ui.css");

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if ("OPTIONS".equals(request.getMethod())) {
            log.info("OPTIONS 요청, 인증을 통과하지 않음: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        if (isExcludedUrl(request.getRequestURI())) {
            log.info("필터 제외 대상 요청: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        if (accessToken != null) {
            checkAccessTokenAndAuthentication(accessToken, filterChain, request, response);
            return;
        }

        // AccessToken이 유효하지 않으면 클라이언트에 401 응답 전송
        sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "AccessToken is invalid");
    }

    private void checkAccessTokenAndAuthentication(String accessToken, FilterChain filterChain, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        jwtService.extractEmail(accessToken)
                .ifPresent(email -> memberRepository.findByEmail(email)
                        .ifPresent(this::saveAuthentication));

        filterChain.doFilter(request, response);
    }

    private void saveAuthentication(Member myMember) {
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(myMember.getRole().getKey())),
                Map.of("email", myMember.getEmail()),
                "email",
                myMember
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customOAuth2User,
                null,
                customOAuth2User.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("SecurityContext Authentication: {}", SecurityContextHolder.getContext().getAuthentication());
    }

    private boolean isExcludedUrl(String requestURI) {
        return EXCLUDED_URLS.stream().anyMatch(requestURI::startsWith);
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> apiResponse = ApiResponse.of(status, message, null, "E_AUTH");

        String jsonResponse = new ObjectMapper().writeValueAsString(apiResponse);

        response.getWriter().write(jsonResponse);
    }
}