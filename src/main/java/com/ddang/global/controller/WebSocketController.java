package com.ddang.global.controller;

import com.ddang.chat.controller.request.ChatMessageRequest;
import com.ddang.chat.service.response.ChatMessageResponse;
import com.ddang.global.api.WebSocketResponse;
import com.ddang.global.exception.annotation.SwaggerExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ddang.global.exception.ErrorCode.MEMBER_NOT_FOUND;

@Tag(name = "WebSocket Chat API", description = "WebSocket을 통한 채팅 관련 명세")
@RestController
public class WebSocketController {

    @PostMapping("/docs/ws/api/v1/chat/message")
    @Operation(
            summary = "채팅 메시지 전송",
            description = "WebSocket을 통해 채팅 메시지를 전송합니다. 메시지를 전송하려면 '/pub/api/v1/chat/message' 경로로 JSON 데이터를 전송하세요. /sub/{Email} 구독 경로로 메세지를 broadcast 합니다. 메세지 읽음 처리에 대해 기존 방식이 비효율적이라고 생각하여 현재 메세지 읽음 처리를 재대로 구현하지 않은 상태입니다.")
    @SwaggerExceptionResponse({MEMBER_NOT_FOUND})
    public WebSocketResponse<ChatMessageResponse> sendMessage(@RequestBody ChatMessageRequest request) {
        ChatMessageResponse chatMessageResponse = null;
        return WebSocketResponse.ok(chatMessageResponse);
    }

    @GetMapping("/sub/{email}")
    @Operation(
            summary = "채팅 url",
            description = " 해당 url을 구독하면, 내가 속한 채팅방으로부터 메세지를 수신합니다. 타 유저에 의해 새로운 채팅방이 생성된 경우에도 메세지를 수신합니다.")
    public WebSocketResponse<List<ChatMessageResponse>> subMemberEmail() {
        List<ChatMessageResponse> chatMessageResponse = null;
        return WebSocketResponse.ok(chatMessageResponse);
    }

}
