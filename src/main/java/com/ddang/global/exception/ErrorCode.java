package com.ddang.global.exception;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum ErrorCode {

    // 401 에러
    UNAUTHORIZED_ATK_ERROR("E_UAT", UNAUTHORIZED, "AccessToken is invalid"),
    UNAUTHORIZED_RTK_ERROR("E_URT", UNAUTHORIZED, "RefreshToken is invalid"),
    EXPIRED_TOKEN_ERROR("E_EXT", UNAUTHORIZED, "JWT 토큰이 만료되었습니다."),
    UNSUPPORTED_TOKEN_ERROR("E_UST", UNAUTHORIZED, "지원하지 않는 JWT 토큰입니다."),
    EMPTY_TOKEN_ERROR("E_EMT", UNAUTHORIZED, "JWT 토큰이 비어 있습니다."),

    // 500 에러
    INTERNAL_SERVER_ERROR("E_SYS", HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다."),

    //DOG
    DOG_NOT_FOUND("E_DNF", NOT_FOUND, "개를 찾을 수 없습니다."),
    MEMBER_NOT_HAVE_DOG("E_MND", NOT_FOUND, "멤버가 개를 소유하고 있지 않습니다."),
    OVER_MAX_DOG("E_OMD", BAD_REQUEST, "멤버가 소유할 수 있는 최대의 개를 소유하고 있습니다."),
    FAMILY_MUST_HAVE_ONE_DOG("E_HOD", BAD_REQUEST, "패밀리는 최소 한 마리의 개를 소유해야 합니다."),
    NAME_NOT_NULL("E_NNN", BAD_REQUEST, "이름은 비워둘 수 없습니다."),
    NAME_EXCEED("E_NEX", BAD_REQUEST, "이름은 최대 10자까지 입력 가능합니다."),
    BREED_NOT_NULL("E_BNN", BAD_REQUEST, "품종은 비워둘 수 없습니다."),
    DATE_MUST_BE_PAST_OR_PRESENT("E_DMPP", BAD_REQUEST, "생년월일은 과거 혹은 현재 날짜여야 합니다."),
    WEIGHT_MINIMUM("E_WMIN", BAD_REQUEST, "몸무게는 최소 1kg 이상이어야 합니다."),
    WEIGHT_MAXIMUM("E_WMAX", BAD_REQUEST, "몸무게는 최대 100kg 이하여야 합니다."),
    WEIGHT_DECIMAL_LIMIT("E_WDL", BAD_REQUEST, "몸무게는 소수점 둘째 자리까지만 가능합니다."),
    GENDER_REQUIRED("E_GR", BAD_REQUEST, "성별은 반드시 입력해야 합니다."),
    NEUTERING_REQUIRED("E_NR", BAD_REQUEST, "중성화 여부는 반드시 입력해야 합니다."),
    COMMENT_SIZE_EXCEED("E_CSE", BAD_REQUEST, "코멘트는 최대 30자까지 입력 가능합니다."),
    DOG_ALREADY_OWNED("E_DAO", BAD_REQUEST, "강아지는 한마리만 소유할 수 있습니다."),


    // S3
    FILE_UPLOAD_FAIL("E_FUF", BAD_REQUEST, "파일 업로드에 실패하였습니다."),
    FILE_DOWNLOAD_FAIL("E_FDF", BAD_REQUEST, "파일 다운로드에 실패하였습니다."),
    FILE_TRANSACTION_FAIL("E_FTF", BAD_REQUEST, "파일 변환에 실패하였습니다."),


    // Member
    MEMBER_NOT_FOUND("E_MEM", NOT_FOUND, "Member not found"),

    // Redis
    REDIS_DATA_SIZE_EXCEEDED_ERROR("E_REDIS", BAD_REQUEST, "Redis에 저장할 데이터 크기가 허용치를 초과했습니다."),
    REDIS_DATA_DELETE_ERROR("E_REDIS", BAD_REQUEST, "Redis에 저장된 데이터 삭제 중 오류가 발생했습니다.");

    private final String code;
    private String message;
    private final HttpStatus status;

    ErrorCode(String code, HttpStatus status, String message) {
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

    public HttpStatus getStatus() {
        return status;
    }

    public void updateServerErrorMessage(String message){
        this.message = message;
    }
}
