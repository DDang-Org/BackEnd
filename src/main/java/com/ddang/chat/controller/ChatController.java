package com.ddang.chat.controller;

import com.ddang.chat.service.ChatService;
import com.ddang.chat.service.response.ChatMessageResponse;
import com.ddang.global.api.ApiResponse;
import com.ddang.global.exception.annotation.SwaggerExceptionResponse;
import com.ddang.member.oauth2.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.ddang.global.exception.ErrorCode.*;

@RestController
@RequestMapping("/api/v1/chat/message")
@Tag(name = "Chat API", description = "채팅 메시지 관련 API")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;


    @GetMapping("/{chatRoomId}")
    @Operation(summary = "채팅방 메시지 조회", description = "특정 채팅방의 메시지를 페이징 형태로 조회합니다.")
    @SwaggerExceptionResponse({CHATROOM_NOT_FOUND, CHATMEMBER_NOT_IN_CHATROOM})
    public ApiResponse<Slice<ChatMessageResponse>> getChatMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastMessageCreatedAt,
            @AuthenticationPrincipal CustomOAuth2User currentUser
    ) {
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("createdAt").descending());


        Slice<ChatMessageResponse> chats = (lastMessageCreatedAt == null) ?
                chatService.findChatsByRoom(chatRoomId, pageRequest, currentUser.getMember()) :
                chatService.findChatsBefore(chatRoomId, lastMessageCreatedAt, pageRequest, currentUser.getMember());

        return ApiResponse.ok(chats);
    }
}
