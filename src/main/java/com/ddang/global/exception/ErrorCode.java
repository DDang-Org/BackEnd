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

    //DOG
    DOG_NOT_FOUND("E_DNF", "BAD_REQUEST", "개를 찾을 수 없습니다."),
    MEMBER_NOT_HAVE_DOG("E_MND", "BAD_REQEUST", "멤버가 개를 소유하고 있지 않습니다."),
    OVER_MAX_DOG("E_OMD", "BAD_REQUEST", "멤버가 소유할 수 있는 최대의 개를 소유하고 있습니다."),
    FAMILY_MUST_HAVE_ONE_DOG("E_HOD", "BAD_REQUEST", "패밀리는 최소 한 마리의 개를 소유해야 합니다."),

    // S3
    FILE_UPLOAD_FAIL("E_FUF", "BAD_REQUEST", "파일 업로드에 실패하였습니다."),
    FILE_DOWNLOAD_FAIL("E_FDF", "BAD_REQUEST", "파일 다운로드에 실패하였습니다."),
    FILE_TRANSACTION_FAIL("E_FTF", "BAD_REQUEST", "파일 변환에 실패하였습니다."),


    // Member
    MEMBER_NOT_FOUND("E_MEM", "NOT_FOUND", "Member not found"),

    // Redis
    REDIS_DATA_SIZE_EXCEEDED_ERROR("E_REDIS", "BAD_REQUEST", "Redis에 저장할 데이터 크기가 허용치를 초과했습니다."),
    REDIS_DATA_DELETE_ERROR("E_REDIS", "BAD_REQUEST", "Redis에 저장된 데이터 삭제 중 오류가 발생했습니다.");

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
