package com.ddang.chat.service;

import com.ddang.chat.service.response.ChatRoomResponse;
import com.ddang.member.entity.Member;

import java.util.List;

public interface ChatRoomService {

    List<ChatRoomResponse> getChatRoomsForAuthenticatedMember(Member member);
}
