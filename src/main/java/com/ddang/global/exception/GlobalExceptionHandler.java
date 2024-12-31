package com.ddang.global.exception;

import com.ddang.global.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    protected ApiResponse<Object> bindException(BindException e) {

        log.error("BindException : {}", e.getMessage());

        return ApiResponse.of(
                HttpStatus.BAD_REQUEST,
                e.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
                null
        );
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ApiResponse<Object> handleHandlerAuthenticationException(AuthenticationException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("AuthenticationException : {}", e.getErrorCode());

        return ApiResponse.of(
                errorCode.getStatus(),
                errorCode.getMessage(),
                null,
                errorCode.getCode()
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    protected ApiResponse<Object> handleGenericException(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        log.error("Unhandled Exception : {}", errorCode);

        return ApiResponse.of(
                errorCode.getStatus(),
                errorCode.getMessage(),
                null,
                errorCode.getCode()
        );
    }

}
