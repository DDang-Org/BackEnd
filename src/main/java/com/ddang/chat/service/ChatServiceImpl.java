package com.ddang.chat.service;

import com.ddang.chat.controller.request.ChatMessageRequest;
import com.ddang.chat.entity.*;
import com.ddang.chat.repository.ChatMemberRepository;
import com.ddang.chat.repository.ChatRepository;
import com.ddang.chat.repository.ChatRoomRepository;
import com.ddang.chat.service.request.ChatMessageKafkaRequest;
import com.ddang.chat.service.request.ChatMessageServiceRequest;
import com.ddang.chat.service.response.ChatMessageResponse;
import com.ddang.global.exception.BadRequestException;
import com.ddang.global.exception.ErrorCode;
import com.ddang.member.entity.Member;
import com.ddang.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatMemberRepository chatMemberRepository;

    @Override
    @Transactional
    public ChatMessageKafkaRequest saveChat(ChatMessageServiceRequest chatMessageServiceRequest) {

        Member sendMember = findMemberByEmailOrThrowException(chatMessageServiceRequest.senderEmail());
        Member rcvMember = findMemberByEmailOrThrowException(chatMessageServiceRequest.receiverEmail());

        ChatRoom chatRoom = getOrCreateChatRoom(sendMember, rcvMember);

        Chat chat = Chat.builder()
                .chatRoom(chatRoom)
                .member(sendMember)
                .chatType(ChatType.TALK)
                .text(chatMessageServiceRequest.message())
                .isRead(IsRead.FALSE)
                .build();

        Chat savedChat = chatRepository.save(chat);

        return ChatMessageKafkaRequest.from(chat, rcvMember);
    }


    // ===================== Helper Methods =====================

    private ChatRoom getOrCreateChatRoom(Member member1, Member member2) {
        Optional<ChatRoom> optionalRoom = chatRoomRepository.findOneToOneChatRoom(member1, member2);
        if (optionalRoom.isPresent()) {
            return optionalRoom.get();
        }
        String roomName = member1.getEmail() + "_" + member2.getEmail();
        ChatRoom newChatRoom = ChatRoom.builder().name(roomName).build();
        newChatRoom = chatRoomRepository.save(newChatRoom);

        ChatMember chatMember1 = ChatMember.builder().member(member1).chatRoom(newChatRoom).build();
        ChatMember chatMember2 = ChatMember.builder().member(member2).chatRoom(newChatRoom).build();
        chatMemberRepository.save(chatMember1);
        chatMemberRepository.save(chatMember2);

        return newChatRoom;
    }



    private Member findMemberByEmailOrThrowException(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Member not found with email {} : {}", email, ErrorCode.MEMBER_NOT_FOUND);
                    return new BadRequestException(ErrorCode.MEMBER_NOT_FOUND);
                });
    }
}
