package com.ddang.chat.controller;

import com.ddang.chat.controller.request.ChatMessageRequest;
import com.ddang.chat.service.ChatKafkaProducer;
import com.ddang.global.aop.ExtractEmail;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketChatController {

    private final ChatKafkaProducer chatKafkaProducer;

    public WebsocketChatController(ChatKafkaProducer chatKafkaProducer) {
        this.chatKafkaProducer = chatKafkaProducer;
    }

    @MessageMapping("/api/v1/chat/message")
    @ExtractEmail
    public void sendMessage(SimpMessageHeaderAccessor headerAccessor, @Valid @Payload ChatMessageRequest chatMessageRequest) {
        // DB 저장, 검증
        chatKafkaProducer.send(chatMessageRequest);
    }
}
