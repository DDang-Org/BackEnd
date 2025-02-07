package com.ddang.chat.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat/message")
@Tag(name = "Chat API", description = "채팅 메시지 관련 API")
@RequiredArgsConstructor
public class ChatController {
}
