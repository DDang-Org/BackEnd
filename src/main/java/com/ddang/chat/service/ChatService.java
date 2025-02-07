package com.ddang.chat.service;

import com.ddang.chat.controller.request.ChatMessageRequest;
import com.ddang.chat.service.request.ChatMessageKafkaRequest;
import com.ddang.chat.service.request.ChatMessageServiceRequest;
import com.ddang.chat.service.response.ChatMessageResponse;
import org.springframework.stereotype.Service;

public interface ChatService {
    ChatMessageKafkaRequest saveChat(ChatMessageServiceRequest chatMessageServiceRequest);

}
