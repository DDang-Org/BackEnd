package com.ddang.member.repository;

import com.ddang.member.entity.FriendRequest;
import com.ddang.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    @Query("SELECT CASE WHEN COUNT(fr) > 0 THEN true ELSE false END FROM FriendRequest fr WHERE fr.sender = :sender AND fr.receiver = :receiver")
    boolean existsFriendRequestBySenderAndReceiver(@Param("sender") Member sender, @Param("receiver") Member receiver);


    @Modifying
    @Query("DELETE FROM FriendRequest fr WHERE fr.sender = :sender AND fr.receiver = :receiver")
    void deleteBySenderAndReceiver(@Param("sender") Member sender, @Param("receiver") Member receiver);

}
