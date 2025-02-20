package com.ddang.member.service;

import com.ddang.IntegrationTestSupport;
import com.ddang.global.entity.Gender;
import com.ddang.member.controller.request.AddFriendRequest;
import com.ddang.member.entity.*;
import com.ddang.member.repository.FriendRepository;
import com.ddang.member.repository.FriendRequestRepository;
import com.ddang.member.repository.MemberRepository;
import com.ddang.member.service.response.FriendListResponse;
import com.ddang.member.service.response.FriendResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
class FriendServiceImplTest extends IntegrationTestSupport {

    @Autowired
    private FriendService friendService;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void createMember(){
        Member member = Member.builder()
                .name("test")
                .email("test@naver.com")
                .role(Role.USER)
                .isMatched(IsMatched.TRUE)
                .address("testAddress")
                .gender(Gender.MALE)
                .familyRole(FamilyRole.BROTHER)
                .provider(Provider.KAKAO)
                .birthDate(LocalDate.of(1999,9,3))
                .profileImg(1)
                .build();

        Member otherMember = Member.builder()
                .name("test2")
                .email("test2@naver.com")
                .role(Role.USER)
                .isMatched(IsMatched.TRUE)
                .address("test2Address")
                .gender(Gender.FEMALE)
                .familyRole(FamilyRole.SISTER)
                .provider(Provider.KAKAO)
                .profileImg(1)
                .birthDate(LocalDate.of(2001,9,3))
                .build();

        memberRepository.saveAll(Arrays.asList(member, otherMember));
    }


    @DisplayName("친구 요청이 없으면 친구 요청을 생성한다.")
    @Test
    void acceptFriendWithoutFriendRequest() {

        //given
        Member member = memberRepository.findByEmail("test@naver.com").get();
        Member otherMember = memberRepository.findByEmail("test2@naver.com").get();
        AddFriendRequest request = new AddFriendRequest(otherMember.getMemberId(), "ACCEPT");

        //when
        friendService.decideFriend(member, request.toService());
        System.out.println(friendRequestRepository.findAll()); // 저장된 데이터 확인


        //then
        assertThat(friendRequestRepository.existsFriendRequestBySenderAndReceiver(member,otherMember))
                .isTrue();
    }


    @DisplayName("상대가 보낸 친구 요청이 존재하면 친구 요청을 삭제 하고 친구를 생성한다.")
    @Test
    void acceptFriendWithFriendRequest() {
        //given
        Member member = memberRepository.findByEmail("test@naver.com").get();
        Member otherMember = memberRepository.findByEmail("test2@naver.com").get();
        AddFriendRequest request = new AddFriendRequest(otherMember.getMemberId(), "ACCEPT");
        FriendRequest friendRequest = FriendRequest.builder()
                .sender(otherMember)
                .receiver(member)
                .build();

        //when
        friendRequestRepository.save(friendRequest);
        friendService.decideFriend(member, request.toService());

        //then
        assertAll("친구 신청, 친구 테이블 확인",
                () -> assertThat(friendRequestRepository.existsFriendRequestBySenderAndReceiver(member, otherMember)).isFalse(),
                () -> assertThat(friendRepository.existsBySenderAndReceiver(member, otherMember)).isTrue()
        );

    }

    @DisplayName("친구 요청이 존재하면 친구 요청을 삭제 한다.")
    @Test
    void denyFriendWithFriendRequest() {
        //given
        Member member = memberRepository.findByEmail("test@naver.com").get();
        Member otherMember = memberRepository.findByEmail("test2@naver.com").get();
        AddFriendRequest request = new AddFriendRequest(otherMember.getMemberId(), "DENY");
        FriendRequest friendRequest = FriendRequest.builder()
                .sender(otherMember)
                .receiver(member)
                .build();


        //when
        friendRequestRepository.save(friendRequest);
        friendService.decideFriend(member, request.toService());

        //then
        assertThat(friendRequestRepository.existsFriendRequestBySenderAndReceiver(member,otherMember))
                .isFalse();
    }


    @Test
    @DisplayName("친구들의 목록을 조회한다.")
    void getFriendList() {
        //given
        Member member = memberRepository.findByEmail("test@naver.com").get();
        Member otherMember = memberRepository.findByEmail("test2@naver.com").get();
        saveFriend(member, otherMember);

        //when
        List<FriendListResponse> friendList = friendService.getFriendList(member);

        //then
        assertThat(friendList)
                .hasSize(1)
                .extracting("memberId", "memberGender", "familyRole", "memberProfileImg", "memberName")
                .containsExactlyInAnyOrder(
                        tuple(otherMember.getMemberId(), otherMember.getGender(), otherMember.getFamilyRole(), otherMember.getProfileImg(), otherMember.getName())
                );

    }

    @Test
    @DisplayName("친구를 삭제한다.")
    void deleteFriend() {
        //given
        Member member = memberRepository.findByEmail("test@naver.com").get();
        Member otherMember = memberRepository.findByEmail("test2@naver.com").get();
        saveFriend(member, otherMember);

        //when
        friendService.deleteFriend(member, otherMember.getMemberId());

        //then
        assertThat(friendRepository.existsBySenderAndReceiver(member, otherMember))
                .isFalse();

    }

    @Test
    @DisplayName("친구아닌 사람을 삭제할 시 에러를 던진다.")
    void deleteFriendWhenIsNotFriendThrowException() {
        //given
        Member member = memberRepository.findByEmail("test@naver.com").get();
        Member otherMember = memberRepository.findByEmail("test2@naver.com").get();

        //when
        assertThatThrownBy(() -> friendService.deleteFriend(member, otherMember.getMemberId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("친구가 아닌 사람을 삭제할 수 없습니다.");
    }


    private void saveFriend(Member member, Member otherMemeber){
        Friend friend = Friend.builder()
                .sender(member)
                .receiver(otherMemeber)
                .build();

        Friend friend2 = Friend.builder()
                .sender(member)
                .receiver(otherMemeber)
                .build();

        friendRepository.saveAll(Arrays.asList(friend ,friend2));
    }
}