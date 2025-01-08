package com.ddang.global.exception;

public enum ErrorCode {

    // S3
    FILE_UPLOAD_FAIL("E_UPL", "BAD_REQUEST", "파일 업로드에 실패하였습니다."),
    FILE_DOWNLOAD_FAIL("E_DOW", "BAD_REQUEST", "파일 다운로드에 실패하였습니다."),
    FILE_TRANSACTION_FAIL("E_TRA", "BAD_REQUEST", "파일 변환에 실패하였습니다."),

    // 401 에러
    UNAUTHORIZED_ERROR("E_AUTH", "UNAUTHORIZED", "AccessToken is invalid"),

    // 500 에러
    INTERNAL_SERVER_ERROR("E_SYS", "INTERNAL_SERVER_ERROR", "알 수 없는 오류가 발생했습니다.");


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
