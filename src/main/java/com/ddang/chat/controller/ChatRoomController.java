package com.ddang.chat.controller;

import com.ddang.chat.service.ChatRoomService;
import com.ddang.chat.service.response.ChatRoomResponse;
import com.ddang.global.api.ApiResponse;
import com.ddang.global.exception.annotation.SwaggerExceptionResponse;
import com.ddang.member.oauth2.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ddang.global.exception.ErrorCode.*;

@RestController
@RequestMapping("/api/v1/chat/rooms")
@RequiredArgsConstructor
@Tag(name = "Chat Room API", description = "채팅방 API")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping
    @Operation(
            summary = "사용자의 채팅방 목록 조회",
            description = """
                    현재 인증된 사용자가 참여 중인 채팅방 목록을 조회합니다.
                    각 채팅방 정보에 추가적으로 마지막 메시지 정보, 읽지 않은 채팅 개수, 채팅방에 참여중인 member 정보가 포함됩니다.
                    """
    )
    @SwaggerExceptionResponse({MEMBER_NOT_FOUND})
    public ApiResponse<List<ChatRoomResponse>> getChatRooms(@AuthenticationPrincipal CustomOAuth2User currentUser) {
        List<ChatRoomResponse> chatRooms = chatRoomService.getChatRoomsForAuthenticatedMember(currentUser.getMember());
        return ApiResponse.ok(chatRooms);
    }
}
