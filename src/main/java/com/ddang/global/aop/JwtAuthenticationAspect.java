package com.ddang.global.aop;

import com.ddang.global.exception.AuthenticationException;
import com.ddang.global.exception.ErrorCode;
import com.ddang.member.jwt.service.JwtService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class JwtAuthenticationAspect {

    private final JwtService jwtService;

    public JwtAuthenticationAspect(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Around("@annotation(ExtractEmail)")
    public Object authenticateToken(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        SimpMessageHeaderAccessor headerAccessor = findHeaderAccessor(args);
        if (headerAccessor == null) {
            throw new AuthenticationException(ErrorCode.EMPTY_ACCESSOR_HEADER);
        }

        String token = jwtService.extractAccessToken(headerAccessor)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.EMPTY_TOKEN_ERROR));
        String email = jwtService.extractEmail(token)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.UNSUPPORTED_TOKEN_ERROR));

        AuthenticationContext.setEmail(email);

        try {
            return joinPoint.proceed();
        } finally {
            AuthenticationContext.clear();
        }
    }

    private SimpMessageHeaderAccessor findHeaderAccessor(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof SimpMessageHeaderAccessor) {
                return (SimpMessageHeaderAccessor) arg;
            }
        }
        return null;
    }
}
