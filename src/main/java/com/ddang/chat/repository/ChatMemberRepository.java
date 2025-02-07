package com.ddang.chat.repository;

import com.ddang.chat.entity.ChatMember;
import com.ddang.chat.entity.ChatRoom;
import com.ddang.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

    @Query("""
                SELECT COUNT(cm) > 0 
                FROM ChatMember cm
                WHERE cm.chatRoom.chatroomId = :chatRoomId 
                  AND cm.member.memberId = :memberId 
                  AND cm.isDeleted = 'FALSE'
            """)
    boolean existsByChatRoomIdAndMemberId(@Param("chatRoomId") Long chatRoomId, @Param("memberId") Long memberId);

    @Query("""
                SELECT cm.member 
                FROM ChatMember cm
                WHERE cm.chatRoom = :chatRoom 
                  AND cm.isDeleted = 'FALSE'
            """)
    List<Member> findMembersByChatRoom(@Param("chatRoom") ChatRoom chatRoom);
}
