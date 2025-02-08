package com.ddang.chat.repository;

import com.ddang.chat.entity.Chat;
import com.ddang.chat.entity.IsRead;
import com.ddang.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("""
                SELECT c 
                FROM Chat c 
                JOIN FETCH c.member 
                JOIN FETCH c.chatRoom 
                WHERE c.isDeleted = 'FALSE' 
                 AND c.chatRoom.chatroomId = :chatRoomId
            """)
    Slice<Chat> findByChatRoomId(Long chatRoomId, Pageable pageable);

    @Query("""
            SELECT c
            FROM Chat c
            WHERE c.chatRoom.chatroomId = :chatRoomId
              AND c.isDeleted = 'FALSE'
              AND c.createdAt < :lastMessageCreatedAt
            """)
    Slice<Chat> findChatsBefore(@Param("chatRoomId") Long chatRoomId, @Param("lastMessageCreatedAt") LocalDateTime lastMessageCreatedAt, Pageable pageable);

    @Query("""
                SELECT c.text 
                FROM Chat c 
                WHERE c.chatRoom.chatroomId = :chatRoomId 
                  AND c.isDeleted = 'FALSE' 
                ORDER BY c.createdAt DESC LIMIT 1
            """)
    String findLastMessageByChatRoom(@Param("chatRoomId") Long chatRoomId);

    @Query("""
                SELECT COUNT(c)
                FROM Chat c
                WHERE c.chatRoom.chatroomId = :chatRoomId
                  AND c.member.memberId <> :memberId
                  AND c.isRead = 'FALSE'
            """)
    Long countUnreadMessagesByChatRoomAndMember(@Param("chatRoomId") Long chatRoomId, @Param("memberId") Long memberId);

    @Modifying
    @Query("""
    UPDATE Chat c 
    SET c.isRead = :trueValue 
    WHERE c.chatRoom.chatroomId = :chatRoomId 
      AND c.member <> :member 
      AND c.isRead = :falseValue
    """)
    int markChatsAsRead(@Param("chatRoomId") Long chatRoomId,
                        @Param("member") Member member,
                        @Param("trueValue") IsRead trueValue,
                        @Param("falseValue") IsRead falseValue);
}