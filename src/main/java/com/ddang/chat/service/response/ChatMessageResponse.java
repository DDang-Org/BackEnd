package com.ddang.chat.service.response;

import com.ddang.chat.entity.ChatType;
import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long chatRoomId,
        Long senderId,
        String text,
        ChatType chatType,
        LocalDateTime timestamp
) {}
