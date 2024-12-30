package com.ddang.chat.entity;

import com.ddang.global.entity.BaseEntity;
import com.ddang.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatType chatType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IsRead isRead;

    @Builder
    private Chat(ChatRoom chatRoom, Member member, ChatType chatType, String text, IsRead isRead) {
        this.chatRoom = chatRoom;
        this.member = member;
        this.chatType = chatType;
        this.text = text;
        this.isRead = isRead != null ? isRead : IsRead.FALSE;
    }
}
