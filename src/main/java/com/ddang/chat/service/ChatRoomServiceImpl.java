package com.ddang.chat.service;

import com.ddang.chat.entity.ChatRoom;
import com.ddang.chat.repository.ChatMemberRepository;
import com.ddang.chat.repository.ChatRepository;
import com.ddang.chat.repository.ChatRoomRepository;
import com.ddang.chat.service.response.ChatRoomResponse;
import com.ddang.global.exception.BadRequestException;
import com.ddang.global.exception.ErrorCode;
import com.ddang.member.entity.Member;
import com.ddang.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService{

    private final ChatMemberRepository chatMemberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getChatRoomsForAuthenticatedMember(Member member) {

        Member currentMember = findMemberByIdOrThrowException(member.getMemberId());

        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByMember(currentMember);

        return chatRooms.stream()
                .map(chatRoom -> {
                    String lastMessage = chatRepository.findLastMessageByChatRoom(chatRoom.getChatroomId());
                    List<Member> allMembers = chatMemberRepository.findMembersByChatRoom(chatRoom);
                    List<Member> otherMembers = allMembers.stream()
                            .filter(m -> !m.getMemberId().equals(currentMember.getMemberId()))
                            .toList();
                    Long unreadCount = chatRepository.countUnreadMessagesByChatRoomAndMember(chatRoom.getChatroomId(), currentMember.getMemberId());
                    return new ChatRoomResponse(chatRoom, lastMessage, unreadCount, otherMembers);
                })
                .toList();
    }


    // ===================== Helper Methods =====================

    private Member findMemberByIdOrThrowException(Long id) {
        return memberRepository.findActiveById(id)
                .orElseThrow(() -> {
                    log.warn("Member not found with id {} : {}", id, ErrorCode.MEMBER_NOT_FOUND);
                    return new BadRequestException(ErrorCode.MEMBER_NOT_FOUND);
                });
    }
}
