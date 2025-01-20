package com.ddang.dog.repository;

import com.ddang.IntegrationTestSupport;
import com.ddang.dog.entity.Dog;
import com.ddang.dog.entity.IsNeutered;
import com.ddang.dog.entity.MemberDog;
import com.ddang.family.entity.Family;
import com.ddang.family.repository.FamilyRepository;
import com.ddang.global.entity.Gender;
import com.ddang.member.entity.*;
import com.ddang.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Transactional
class MemberDogRepositoryTest extends IntegrationTestSupport {

    @Autowired
    FamilyRepository familyRepository;

    @Autowired
    MemberDogRepository memberDogRepository;

    @Autowired
    DogRepository dogRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("MemberDog 을 멤버와 강아지 Id 로 조회 한다")
    void findByDogIdAndMemberId() {
        //given
        Family family = createAndSaveFamily();
        Member member = createAndSaveMember(family);
        Dog dog = createAndSaveDog(family);
        createAndSaveMemberDog(member, dog);

        //when
        MemberDog findMemberDog = memberDogRepository.findByDogIdAndMemberId(dog.getDogId(), member.getMemberId()).get();

        //then
        assertThat(findMemberDog).extracting("dog", "member")
                .containsExactlyInAnyOrder(
                        dog, member
                );
    }

    @Test
    @DisplayName("MemberDog 을 Soft Delete 처리 한다.")
    void softDeleteByDogId() {
        //given
        Family family = createAndSaveFamily();
        Member member = createAndSaveMember(family);
        Dog dog = createAndSaveDog(family);
        createAndSaveMemberDog(member, dog);

        //when
        memberDogRepository.softDeleteByDogId(dog.getDogId());

        //then
        assertThat(memberDogRepository.findByDogIdAndMemberId(dog.getDogId(), member.getMemberId())).isEmpty();
    }

    @Test
    @DisplayName("MemberDog 을 Member 로 모두 조회한다.")
    void findAllByMember() {
        //given
        Family family = createAndSaveFamily();
        Member member = createAndSaveMember(family);
        Dog dog = createAndSaveDog(family);
        createAndSaveMemberDog(member, dog);

        //when
        List<MemberDog> memberDogs = memberDogRepository.findAllByMember(member.getMemberId());

        //then
        assertThat(memberDogs).hasSize(1)
                .extracting("dog" ,"member")
                .containsExactlyInAnyOrder(
                        tuple(dog, member)
                );
    }

    private Member createAndSaveMember(Family family){
        Member member = Member.builder()
                .memberName("test2")
                .email("test2@naver.com")
                .role(Role.USER)
                .isMatched(IsMatched.TRUE)
                .address("test2Address")
                .memberBirthDate(LocalDate.of(2000,5,2))
                .memberGender(Gender.FEMALE)
                .familyRole(FamilyRole.ELDER_SISTER)
                .family(family)
                .provider(Provider.GOOGLE)
                .memberProfileImg("")
                .build();

        memberRepository.save(member);

        return member;
    }

    private Dog createAndSaveDog(Family family) {
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
        return dog;
    }

    private Family createAndSaveFamily(){

        Family family = Family.create();
        familyRepository.save(family);

        return family;
    }

    private MemberDog createAndSaveMemberDog(Member member, Dog dog){
        MemberDog memberDog = MemberDog.builder()
                .dog(dog)
                .member(member)
                .build();

        memberDogRepository.save(memberDog);

        return memberDog;
    }
}