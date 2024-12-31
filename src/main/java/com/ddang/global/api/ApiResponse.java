package com.ddang.global.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {

    private String code;

    private String status;

    private String message;

    private T data;

    public ApiResponse(String status, String message, T data, String code) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> of(HttpStatus httpStatus, String message, T data, String code) {
        return new ApiResponse<>(httpStatus.getReasonPhrase(), message, data, code);
    }

    public static <T> ApiResponse<T> of(HttpStatus httpStatus, T data, String code) {
        return of(httpStatus, httpStatus.name(), data, code);
    }

    public static <T> ApiResponse<T> of(String httpStatus, String message, T data, String code) {
        return new ApiResponse<>(httpStatus, message, data, code);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return of(HttpStatus.OK ,data, "SUC");
    }

    public static <T> ApiResponse<T> created(T data) {
        return of(HttpStatus.CREATED, data, "CRE");
    }

    public static ApiResponse<Void> noContent() {
        return of(HttpStatus.NO_CONTENT, "No Content", null, "NCT");
    }
}
