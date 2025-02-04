package com.ddang.member.service;

import com.ddang.member.entity.Member;
import com.ddang.member.service.request.AddFriendServiceRequest;
import com.ddang.member.service.response.FriendListResponse;
import com.ddang.member.service.response.FriendResponse;
import com.ddang.member.service.response.MemberResponse;

import java.util.List;

public interface FriendService {
    MemberResponse decideFriend(Member member, AddFriendServiceRequest addFriendServiceRequest);

    List<FriendListResponse> getFriendList(Member member);

//    FriendResponse getFriend(Member member, Long memberId);

    void deleteFriend(Member member, Long memberId);
}
