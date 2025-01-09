package com.ddang.global.exception;

public class BadRequestException extends CustomException{

    public BadRequestException(ErrorCode errorCode) { super(errorCode);}
}
