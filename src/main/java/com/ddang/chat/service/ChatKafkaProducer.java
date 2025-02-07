package com.ddang.chat.service;

import com.ddang.chat.controller.request.ChatMessageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatKafkaProducer {

    private final KafkaTemplate<String, ChatMessageRequest> kafkaTemplate;

    @Value("${kafka.topic.chat-messages}")
    private String chatTopic;

    public ChatKafkaProducer(KafkaTemplate<String, ChatMessageRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(ChatMessageRequest chatMessageRequest) {
        kafkaTemplate.send(chatTopic, chatMessageRequest);
    }
}
