package com.ddang.chat.service;

import com.ddang.chat.controller.request.ChatMessageRequest;
import com.ddang.chat.service.response.ChatMessageResponse;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ChatKafkaListener {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatKafkaListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(topics = "${kafka.topic.chat-messages}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(ChatMessageRequest chatMessageRequest) {
        ChatMessageResponse chatMessageResponse = new ChatMessageResponse(
                chatMessageRequest.chatRoomId(),
                chatMessageRequest.senderId(),
                chatMessageRequest.text(),
                chatMessageRequest.chatType(),
                LocalDateTime.now()
        );

        String destination = "/queue/chat/" + chatMessageRequest.chatRoomId();
        messagingTemplate.convertAndSend(destination, chatMessageResponse);
    }
}
