package com.ddang.chat.service.request;

import com.ddang.chat.entity.Chat;
import com.ddang.chat.entity.ChatType;
import com.ddang.global.entity.Gender;
import com.ddang.member.entity.FamilyRole;
import com.ddang.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ChatMessageKafkaRequest(
        Long sendMemberId,
        String sendMemberName,
        String sendEmail,
        Gender sendMemberGender,
        FamilyRole sendFamilyRole,
        int sendMemberProfileImg,
        String rcvEmail,
        Long chatId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long chatRoomId,
        ChatType chatType,
        String text
) {
    public static ChatMessageKafkaRequest from(Chat chat, Member receiver) {
        Member sender = chat.getMember();

        return new ChatMessageKafkaRequest(
                sender.getMemberId(),
                sender.getName(),
                sender.getEmail(),
                sender.getGender(),
                sender.getFamilyRole(),
                sender.getProfileImg(),
                receiver.getEmail(),
                chat.getChatId(),
                chat.getCreatedAt(),
                chat.getUpdatedAt(),
                chat.getChatRoom().getChatroomId(),
                chat.getChatType(),
                chat.getText()
        );
    }
}
