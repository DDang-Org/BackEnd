package com.ddang.global.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "API 응답 객체")
@Getter
public class WebSocketResponse<T> {
    @Schema(description = "Ws 응답 코드", example = "1000")
    private int code;

    @Schema(description = "응답 메시지", example = "success")
    private String message;

    @Schema(description = "응답 데이터")
    private T data;


    private WebSocketResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> WebSocketResponse<T> of(int code, String message, T data) {
        return new WebSocketResponse<>(code, message, data);
    }

    public static <T> WebSocketResponse<T> ok(T data) {
        return of(1000, "success", data);
    }

}
