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

    //BAD_REQUEST
    INVALID_PAGE_NUMBER("E_IPN", BAD_REQUEST, "페이지 번호는 0 이상이어야 합니다."),

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
    NOT_MEMBER_DOG("E_NMD", BAD_REQUEST, "강아지의 소유자가 아닙니다."),

    //Family
    FAMILY_NOT_FOUND("E_FNF", NOT_FOUND, "패밀리댕을 찾을 수 없습니다."),
    MEMBER_NOT_IN_FAMILY("E_MNF", BAD_REQUEST, "패밀리댕에 속하지 않은 사용자입니다."),
    MEMBER_IN_FAMILY("E_MIF", BAD_REQUEST, "패밀리댕에 이미 속해 있는 사용자입니다."),
    MEMBER_HAVE_DOG("E_MHD", BAD_REQUEST, "강아지를 소유하고 있다면 패밀리댕에 가입할 수 없습니다."),
    INVALID_INVITE_CODE("E_IIC", BAD_REQUEST, "유효하지 않은 초대 코드입니다."),
    MEMBER_NOT_FAMILY_BOSS("E_MFB", BAD_REQUEST, "패밀리댕 대표가 아닌 사용자입니다."),
    SELF_REMOVE_NOT_ALLOWED("E_SRA", BAD_REQUEST, "본인을 추방할 수 없습니다."),
    INVALID_FAMILY_MEMBER("E_IFM", BAD_REQUEST, "잘못된 패밀리댕 사용자입니다."),
    INVALID_ACTION_FAMILY_BOSS("E_IAF", BAD_REQUEST, "패밀리댕 대표는 할 수 없습니다."),

    //Chat
    CHATROOM_NOT_FOUND("E_CNF", NOT_FOUND, "해당 채팅방을 찾을 수 없습니다."),
    CHATMEMBER_NOT_IN_CHATROOM("E_CNC", BAD_REQUEST, "해당 채팅방을 찾을 수 없습니다."),


    // S3
    FILE_UPLOAD_FAIL("E_FUF", BAD_REQUEST, "파일 업로드에 실패하였습니다."),
    FILE_DOWNLOAD_FAIL("E_FDF", BAD_REQUEST, "파일 다운로드에 실패하였습니다."),
    FILE_TRANSACTION_FAIL("E_FTF", BAD_REQUEST, "파일 변환에 실패하였습니다."),


    // Member
    MEMBER_NOT_FOUND("E_MEM", NOT_FOUND, "멤버를 찾을 수 없습니다."),
    INVALID_EMAIL("E_IEM", BAD_REQUEST, "이메일 형식이 올바르지 않습니다."),
    PROVIDER_NOT_NULL("E_PNN", BAD_REQUEST, "OAuth2 서버 제공자를 입력해주세요."),
    MEMBER_NAME_NOT_NULL("E_MNN", BAD_REQUEST, "멤버 이름은 비워둘 수 없습니다."),
    MEMBER_GENDER_NOT_NULL("E_MGN", BAD_REQUEST, "멤버 성별은 비워둘 수 없습니다."),
    MEMBER_ADDRESS_NOT_NULL("E_MAN", BAD_REQUEST, "멤버 주소는 비워둘 수 없습니다."),
    MEMBER_FAMILY_ROLE_NOT_NULL("E_MFR", BAD_REQUEST, "멤버 가족 역할은 비워둘 수 없습니다."),
    MEMBER_PROFILE_IMG_NOT_NULL("E_MPI", BAD_REQUEST, "멤버 프로필 이미지는 비워둘 수 없습니다."),
    MEMBER_BIRTH_DATE_MUST_BE_PAST_OR_PRESENT("E_MBD", BAD_REQUEST, "멤버 생년월일은 과거 혹은 현재 날짜여야 합니다."),
    INVALID_IS_MATCHED("E_IIM", BAD_REQUEST, "isMatched는 반드시 TRUE 혹은 FALSE여야 합니다."),

    // Block
    BLOCK_NOT_FOUND("E_BNF", NOT_FOUND, "차단 내역을 찾을 수 없습니다."),
    BLOCKED_MEMBER_IS_FAMILY_MEMBER("E_BFM", BAD_REQUEST, "가족 멤버는 차단할 수 없습니다."),
    ALREADY_BLOCKED_MEMBER("E_ABM", BAD_REQUEST, "이미 차단된 멤버입니다."),

    // NOTIFICATION
    NOTIFICATION_SETTINGS_NOT_FOUND("E_NSNF", NOT_FOUND, "알림 설정을 찾을 수 없습니다."),
    INVALID_NOTIFICATION_TYPE("E_INT", BAD_REQUEST, "알림 타입은 반드시 CHAT, FRIEND, WALK 중 하나여야 합니다."),
    INVALID_IS_AGREED("E_IIA", BAD_REQUEST, "isAgreed는 반드시 TRUE 혹은 FALSE여야 합니다."),

    //WebSocket
    EMPTY_ACCESSOR_HEADER("E_EAH", NOT_FOUND, "Accessor Header 를 찾을 수 없습니다."),

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
