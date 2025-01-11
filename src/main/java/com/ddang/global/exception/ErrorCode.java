package com.ddang.global.exception;

public enum ErrorCode {

    // 401 에러
    UNAUTHORIZED_ATK_ERROR("E_AUTH", "UNAUTHORIZED", "AccessToken is invalid"),
    UNAUTHORIZED_RTK_ERROR("E_AUTH", "UNAUTHORIZED", "RefreshToken is invalid"),
    EXPIRED_TOKEN_ERROR("E_AUTH", "UNAUTHORIZED", "JWT 토큰이 만료되었습니다."),
    UNSUPPORTED_TOKEN_ERROR("E_AUTH", "UNAUTHORIZED", "지원하지 않는 JWT 토큰입니다."),
    EMPTY_TOKEN_ERROR("E_AUTH", "UNAUTHORIZED", "JWT 토큰이 비어 있습니다."),

    // 500 에러
    INTERNAL_SERVER_ERROR("E_SYS", "INTERNAL_SERVER_ERROR", "알 수 없는 오류가 발생했습니다."),

    // Member
    MEMBER_NOT_FOUND("E_MEM", "NOT_FOUND", "Member not found");

    private final String code;
    private final String message;
    private final String status;

    ErrorCode(String code, String status, String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }
}
