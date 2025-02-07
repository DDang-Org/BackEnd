package com.ddang.chat.service;

import com.ddang.chat.service.request.ChatMessageKafkaRequest;
import com.ddang.chat.service.request.ChatMessageServiceRequest;
import com.ddang.chat.service.response.ChatMessageResponse;
import com.ddang.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;

public interface ChatService {
    ChatMessageKafkaRequest saveChat(ChatMessageServiceRequest chatMessageServiceRequest);

    Slice<ChatMessageResponse> findChatsByRoom(Long chatRoomId, Pageable pageable, Member member);

    Slice<ChatMessageResponse> findChatsBefore(Long chatRoomId, LocalDateTime lastMessageCreatedAt, Pageable pageable, Member member);
}
