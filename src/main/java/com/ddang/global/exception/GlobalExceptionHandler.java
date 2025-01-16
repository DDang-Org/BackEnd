package com.ddang.global.exception;

import com.ddang.global.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
                "Error",
                e.getBindingResult().getAllErrors().get(0).getDefaultMessage()
        );
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e){
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.from(errorCode));

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorCode errorCode =  ErrorCode.INTERNAL_SERVER_ERROR;
        errorCode.updateServerErrorMessage(e.getMessage());

        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.from(errorCode));
    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(BadRequestException.class)
//    protected ApiResponse<Object> bindException(BadRequestException e) {
//        ErrorCode errorCode = e.getErrorCode();
//        log.error("AuthenticationException : {}", e.getErrorCode());
//
//        return ApiResponse.of(
//                errorCode.getStatus(),
//                errorCode.getMessage(),
//                null,
//                errorCode.getCode()
//        );
//    }
//
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    @ExceptionHandler(AuthenticationException.class)
//    public ApiResponse<Object> handleHandlerAuthenticationException(AuthenticationException e) {
//        ErrorCode errorCode = e.getErrorCode();
//        log.error("AuthenticationException : {}", e.getErrorCode());
//
//        return ApiResponse.of(
//                errorCode.getStatus(),
//                errorCode.getMessage(),
//                null,
//                errorCode.getCode()
//        );
//    }
//
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler(Exception.class)
//    protected ApiResponse<Object> handleGenericException(Exception e) {
//        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
//        log.error("Unhandled Exception : {}", errorCode);
//
//        return ApiResponse.of(
//                errorCode.getStatus(),
//                errorCode.getMessage(),
//                null,
//                errorCode.getCode()
//        );
//    }
//
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @ExceptionHandler(MemberException.class)
//    protected ApiResponse<Object> handleMemberNotFoundException(MemberException e) {
//        ErrorCode errorCode = e.getErrorCode();
//        log.error("MemberException : {}", errorCode);
//
//        return ApiResponse.of(
//                HttpStatus.NOT_FOUND,
//                errorCode.getMessage(),
//                null,
//                errorCode.getCode()
//        );
//    }


}
