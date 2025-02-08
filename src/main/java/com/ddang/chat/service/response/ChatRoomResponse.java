package com.ddang.chat.service.response;

import com.ddang.chat.entity.ChatRoom;
import com.ddang.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "채팅방 응답 데이터")
public record ChatRoomResponse(
        @Schema(description = "채팅방 ID", example = "1")
        Long chatRoomId,
        @Schema(description = "채팅방 이름", example = "Team Chat")
        String name,
        @Schema(description = "마지막 메시지", example = "안녕하세요!")
        String lastMessage,
        @Schema(description = "읽지 않은 메시지 개수", example = "3")
        Long unreadMessageCount,
        @Schema(description = "채팅방 참여자")
        List<ChatMemberInfo> members
) {
    public ChatRoomResponse(ChatRoom chatRoom, String lastMessage, Long unreadMessageCount, List<Member> members) {
        this(
                chatRoom.getChatroomId(),
                members.stream()
                        .map(Member::getName)
                        .collect(Collectors.joining(", ")),
                lastMessage,
                unreadMessageCount,
                members.stream()
                        .map(ChatMemberInfo::from)
                        .collect(Collectors.toList())
        );
    }
}