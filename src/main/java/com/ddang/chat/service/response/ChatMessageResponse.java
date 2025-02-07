package com.ddang.chat.service.response;

import com.ddang.chat.entity.Chat;
import com.ddang.chat.entity.ChatType;
import com.ddang.chat.entity.IsRead;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        @Schema(description = "메시지 ID", example = "123")
        Long chatId,

        @Schema(description = "메시지 생성 시간", example = "2024-11-21T12:00:00")
        LocalDateTime createdAt,

        @Schema(description = "메시지 수정 시간", example = "2024-11-21T12:05:00")
        LocalDateTime updatedAt,

        @Schema(description = "채팅방 ID", example = "1")
        Long chatRoomId,

        @Schema(description = "작성자 정보")
        ChatMemberInfo memberInfo,

        @Schema(description = "채팅 타입", example = "TALK")
        ChatType chatType,

        @Schema(description = "읽음 여부", example = "TRUE")
        IsRead isRead,

        @Schema(description = "메시지 내용", example = "안녕하세요!")
        String text
) {
    public static ChatMessageResponse from(Chat chat) {
        return new ChatMessageResponse(
                chat.getChatId(),
                chat.getCreatedAt(),
                chat.getUpdatedAt(),
                chat.getChatRoom().getChatroomId(),
                ChatMemberInfo.from(chat.getMember()),
                chat.getChatType(),
                chat.getIsRead(),
                chat.getText()
        );
    }

    public static ChatMessageResponse from(com.ddang.chat.service.request.ChatMessageKafkaRequest request) {
        return new ChatMessageResponse(
                request.chatId(),
                request.createdAt(),
                request.updatedAt(),
                request.chatRoomId(),
                ChatMemberInfo.fromKafka(request),
                request.chatType(),
                request.isRead(),
                request.text()
        );
    }
}
