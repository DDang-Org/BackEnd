package com.ddang.family.service;

import com.ddang.IntegrationTestSupport;
import com.ddang.dog.entity.Dog;
import com.ddang.dog.entity.IsNeutered;
import com.ddang.dog.repository.DogRepository;
import com.ddang.family.entity.Family;
import com.ddang.family.repository.FamilyRepository;
import com.ddang.family.service.response.FamilyDogResponse;
import com.ddang.family.service.response.FamilyMemberResponse;
import com.ddang.global.entity.Gender;
import com.ddang.member.entity.*;
import com.ddang.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
class FamilyServiceImplTest extends IntegrationTestSupport {

    @Autowired
    private FamilyService familyService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private DogRepository dogRepository;

    private Member member;
    private Family family;

    @BeforeEach
    void setup() {
        member = Member.builder()
                .memberName("testUser")
                .email("test@naver.com")
                .role(Role.USER)
                .memberBirthDate(LocalDate.of(1995, 5, 10))
                .address("Seoul, Korea")
                .memberProfileImg("profile.png")
                .memberGender(Gender.MALE)
                .familyRole(FamilyRole.ELDER_BROTHER)
                .isMatched(IsMatched.FALSE)
                .provider(Provider.KAKAO)
                .role(Role.USER)
                .build();

        memberRepository.save(member);

        family = Family.create(member.getMemberId());
        familyRepository.save(family);

        member.updateFamily(family);
        memberRepository.save(member);

        familyRepository.flush();
        memberRepository.flush();
    }

    @Test
    @Disabled
    @DisplayName("가족 초대 코드를 생성한다.")
    void createInviteCode() {

        memberRepository.flush();
        familyRepository.flush();

        // when
        var inviteCodeResponse = familyService.createInviteCode(member);

        // then
        assertAll(
                () -> assertThat(inviteCodeResponse).isNotNull(),
                () -> assertThat(inviteCodeResponse.inviteCode()).isNotEmpty(),
                () -> assertThat(inviteCodeResponse.expiresInSeconds()).isGreaterThan(0)
        );
    }

    @Test
    @Disabled
    @DisplayName("초대 코드를 사용하여 가족에 추가한다.")
    void addMemberToFamily() {
        // given
        var inviteCodeResponse = familyService.createInviteCode(member);
        String inviteCode = inviteCodeResponse.inviteCode();

        Member newMember = Member.builder()
                .memberName("newUser")
                .email("new@naver.com")
                .role(Role.USER)
                .memberBirthDate(LocalDate.of(2000, 3, 15))
                .address("Busan, Korea")
                .memberProfileImg("new_profile.png")
                .memberGender(Gender.FEMALE)
                .familyRole(FamilyRole.ELDER_SISTER)
                .isMatched(IsMatched.FALSE)
                .provider(Provider.GOOGLE)
                .role(Role.USER)
                .build();

        memberRepository.save(newMember);

        // when
        var familyResponse = familyService.addMemberToFamily(inviteCode, newMember);

        // then
        assertThat(familyResponse.familyId()).isEqualTo(family.getFamilyId());
    }

    @Test
    @Disabled
    @DisplayName("가족의 모든 강아지를 조회한다.")
    void getFamilyDogs() {
        // given
        Dog dog = Dog.builder()
                .name("choco")
                .gender(Gender.MALE)
                .profileImg("url")
                .birthDate(LocalDate.of(2023,5,7))
                .breed("sigol")
                .family(family)
                .weight(BigDecimal.valueOf(3.7))
                .comment("kind")
                .isNeutered(IsNeutered.TRUE)
                .build();

        dogRepository.save(dog);

        // when
        var dogs = familyService.getFamilyDogs(family.getFamilyId().toString());

        // then
        assertThat(dogs).hasSize(1);
    }

    @Test
    @Disabled
    @DisplayName("로그인한 멤버의 가족 강아지들을 조회한다.")
    void getMyFamilyDogs() {
        // given
        Dog dog = Dog.builder()
                .name("choco")
                .gender(Gender.MALE)
                .profileImg("url")
                .birthDate(LocalDate.of(2023,5,7))
                .breed("sigol")
                .family(family)
                .weight(BigDecimal.valueOf(3.7))
                .comment("kind")
                .isNeutered(IsNeutered.TRUE)
                .build();

        dogRepository.save(dog);

        // when
        List<FamilyDogResponse> responses = familyService.getMyFamilyDogs(member);

        // then
        assertThat(responses).hasSize(1)
                .extracting("dogName")
                .contains("buddy");
    }

    @Test
    @DisplayName("로그인한 멤버의 가족 구성원을 조회한다.")
    void getMyFamily() {
        // when
        List<FamilyMemberResponse> responses = familyService.getMyFamily(member);

        // then
        assertThat(responses).hasSize(1)
                .extracting("memberName")
                .contains("testUser");
    }

    @Test
    @DisplayName("가족 대표를 지정한다.")
    void assignFamilyRepresentative() {
        // given
        Member newRep = Member.builder()
                .memberName("leader")
                .email("leader@naver.com")
                .role(Role.USER)
                .memberBirthDate(LocalDate.of(1990, 8, 25))
                .address("Seoul, Korea")
                .memberProfileImg("leader_profile.png")
                .memberGender(Gender.MALE)
                .familyRole(FamilyRole.ELDER_BROTHER)
                .isMatched(IsMatched.FALSE)
                .provider(Provider.GOOGLE)
                .role(Role.USER)
                .family(family)
                .build();

        memberRepository.save(newRep);

        newRep.updateFamily(family);
        memberRepository.save(newRep);

        // when
        familyService.assignFamilyRepresentative(member, newRep.getMemberId());

        // then
        assertThat(family.getRepresentativeMemberId()).isEqualTo(newRep.getMemberId());
    }

    @Test
    @DisplayName("가족 구성원을 제거한다.")
    void removeMemberFromFamily() {
        // given
        Member newMember = Member.builder()
                .memberName("removedUser")
                .email("remove@naver.com")
                .role(Role.USER)
                .memberBirthDate(LocalDate.of(1998, 7, 20))
                .address("Incheon, Korea")
                .memberProfileImg("removed_profile.png")
                .memberGender(Gender.FEMALE)
                .familyRole(FamilyRole.ELDER_SISTER)
                .isMatched(IsMatched.FALSE)
                .provider(Provider.GOOGLE)
                .role(Role.USER)
                .family(family)
                .build();

        memberRepository.save(newMember);

        newMember.updateFamily(family);
        memberRepository.save(newMember);

        // when
        familyService.removeMemberFromFamily(newMember.getMemberId(), member);
        memberRepository.flush();

        // then
        assertThat(memberRepository.findById(newMember.getMemberId()).get().getFamily()).isNull();
    }

    @Test
    @DisplayName("가족을 탈퇴한다.")
    void leaveFamily() {

        // given
        Member newMember = Member.builder()
                .memberName("removedUser")
                .email("remove@naver.com")
                .role(Role.USER)
                .memberBirthDate(LocalDate.of(1998, 7, 20))
                .address("Incheon, Korea")
                .memberProfileImg("removed_profile.png")
                .memberGender(Gender.FEMALE)
                .familyRole(FamilyRole.ELDER_SISTER)
                .isMatched(IsMatched.FALSE)
                .provider(Provider.GOOGLE)
                .role(Role.USER)
                .family(family)
                .build();

        memberRepository.save(newMember);

        newMember.updateFamily(family);
        memberRepository.save(newMember);
        memberRepository.flush();

        // when
        familyService.leaveFamily(newMember);
        memberRepository.flush();

        // then
        assertThat(memberRepository.findById(newMember.getMemberId()).get().getFamily()).isNull();
    }
}
