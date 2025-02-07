package com.ddang.chat.controller;

import com.ddang.chat.controller.request.ChatMessageRequest;
import com.ddang.chat.service.ChatKafkaProducer;
import com.ddang.chat.service.ChatService;
import com.ddang.chat.service.request.ChatMessageKafkaRequest;
import com.ddang.chat.service.response.ChatMessageResponse;
import com.ddang.global.aop.AuthenticationContext;
import com.ddang.global.aop.ExtractEmail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebsocketChatController {

    private final ChatKafkaProducer chatKafkaProducer;
    private final ChatService chatService;

    @MessageMapping("/api/v1/chat/message")
    @ExtractEmail
    public void sendMessage(SimpMessageHeaderAccessor headerAccessor, @Valid @Payload ChatMessageRequest chatMessageRequest) {
        ChatMessageKafkaRequest chatMessageKafkaRequest = chatService.saveChat(chatMessageRequest.toServiceRequest(AuthenticationContext.getEmail()));
        chatKafkaProducer.send(chatMessageKafkaRequest);
    }
}
