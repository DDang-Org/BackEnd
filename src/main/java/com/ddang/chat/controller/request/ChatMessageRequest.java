package com.ddang.chat.controller.request;

import com.ddang.chat.entity.ChatType;

public record ChatMessageRequest(
        Long chatRoomId,
        Long senderId,
        String text,
        ChatType chatType
) {}
