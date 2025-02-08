package com.ddang.member.repository;

import com.ddang.member.entity.Friend;
import com.ddang.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRepository  extends JpaRepository<Friend, Long> {

    @Query("""
    SELECT f.receiver
    FROM Friend f
    WHERE f.sender = :sender
    """)
    List<Member> findAllFriendsBySender(Member sender);

    @Modifying
    @Query("""
    DELETE FROM Friend f
    WHERE (f.sender = :member AND f.receiver = :otherMember)
       OR (f.sender = :otherMember AND f.receiver = :member)
""")
    void deleteBySenderAndReceiver(Member member, Member otherMember);

    @Query("""
    SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END
    FROM Friend f
    WHERE (f.sender = :sender AND f.receiver = :receiver)
       OR (f.sender = :receiver AND f.receiver = :sender)
""")
    boolean existsBySenderAndReceiver(@Param("sender") Member sender, @Param("receiver") Member receiver);

}
