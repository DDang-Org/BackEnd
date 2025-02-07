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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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


    @Override
    @Transactional
    public Slice<ChatMessageResponse> findChatsByRoom(Long chatRoomId, Pageable pageable, Member member) {

        Member currentMember = checkValidate(chatRoomId, member.getEmail());

        chatRepository.markChatsAsRead(chatRoomId, currentMember, IsRead.TRUE, IsRead.FALSE);

        Slice<Chat> chats = chatRepository.findByChatRoomId(chatRoomId, pageable);

//        String topic = "topic-chat-" + chatRoomId;
//        chatProducer.sendReadEvent(topic, new ChatReadServiceRequest(chatRoomId, currentMember.getEmail(), null));

        List<ChatMessageResponse> reversedResponses = new ArrayList<>(chats.getContent()
                .stream()
                .map(ChatMessageResponse::from)
                .toList());
        Collections.reverse(reversedResponses);

        return new SliceImpl<>(reversedResponses, pageable, chats.hasNext());
    }

    @Override
    @Transactional
    public Slice<ChatMessageResponse> findChatsBefore(Long chatRoomId, LocalDateTime lastMessageCreatedAt, Pageable pageable, Member member){
        checkValidate(chatRoomId, member.getEmail());
        Slice<Chat> chats = chatRepository.findChatsBefore(chatRoomId, lastMessageCreatedAt, pageable);
        List<ChatMessageResponse> reversedResponses = new ArrayList<>(chats.getContent()
                .stream()
                .map(ChatMessageResponse::from)
                .toList());
        Collections.reverse(reversedResponses);

        return new SliceImpl<>(reversedResponses, pageable, chats.hasNext());
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

    private Member checkValidate(Long chatRoomId, String email){
        findChatRoomByIdOrThrowException(chatRoomId);

        Member member = findMemberByEmailOrThrowException(email);

        if(!chatMemberRepository.existsByChatRoomIdAndMemberId(chatRoomId, member.getMemberId())){
            throw new BadRequestException(ErrorCode.CHATMEMBER_NOT_IN_CHATROOM);
        }

        return member;
    }

    private Member findMemberByEmailOrThrowException(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Member not found with email {} : {}", email, ErrorCode.MEMBER_NOT_FOUND);
                    return new BadRequestException(ErrorCode.MEMBER_NOT_FOUND);
                });
    }

    private ChatRoom findChatRoomByIdOrThrowException(Long id) {
        return chatRoomRepository.findActiveById(id)
                .orElseThrow(() -> {
                    log.warn("Chatroom not found with id {} : {}", id, ErrorCode.CHATROOM_NOT_FOUND);
                    return new BadRequestException(ErrorCode.CHATROOM_NOT_FOUND);
                });
    }
}
