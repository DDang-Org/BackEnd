package com.ddang.global.exception;

public class RedisException extends CustomException{

    public RedisException(ErrorCode errorCode) { super(errorCode);}
}
