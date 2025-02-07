package com.ddang.chat.repository;

import com.ddang.chat.entity.ChatRoom;
import com.ddang.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
        SELECT c 
        FROM ChatRoom c 
        WHERE c.isDeleted = 'false' 
          AND c.chatroomId = :id
    """)
    Optional<ChatRoom> findActiveById(Long id);

    @Query("""
    SELECT c 
    FROM ChatRoom c 
    WHERE c.isDeleted = 'false'
      AND (
          SELECT COUNT(cm) 
          FROM ChatMember cm 
          WHERE cm.chatRoom = c 
            AND cm.member IN (:member1, :member2) 
            AND cm.isDeleted = 'false'
      ) = 2
      AND (
          SELECT COUNT(cm2)
          FROM ChatMember cm2
          WHERE cm2.chatRoom = c 
            AND cm2.isDeleted = 'false'
      ) = 2
""")
    Optional<ChatRoom> findOneToOneChatRoom(@Param("member1") Member member1,
                                            @Param("member2") Member member2);

    @Query("""
        SELECT DISTINCT c 
        FROM ChatRoom c 
        JOIN ChatMember cm ON cm.chatRoom = c 
        WHERE cm.member = :member 
          AND c.isDeleted = 'FALSE' 
          AND cm.isDeleted = 'FALSE'
    """)
    List<ChatRoom> findChatRoomsByMember(@Param("member") Member member);
}