package com.ddang.chat.controller;

import com.ddang.chat.controller.request.ChatMessageRequest;
import com.ddang.chat.service.ChatKafkaProducer;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketChatController {

    private final ChatKafkaProducer chatKafkaProducer;

    public WebsocketChatController(ChatKafkaProducer chatKafkaProducer) {
        this.chatKafkaProducer = chatKafkaProducer;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequest chatMessageRequest) {
        // DB 저장, 검증
        chatKafkaProducer.send(chatMessageRequest);
    }
}
