package com.ddang.global.exception;

public record ErrorResponse(
        String code,
        String Status,
        String message,
        String data
) {
    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getStatus().name(), errorCode.getMessage(), "Error");
    }

}
