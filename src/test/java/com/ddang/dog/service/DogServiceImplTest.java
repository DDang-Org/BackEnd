package com.ddang.dog.service;

import com.ddang.IntegrationTestSupport;
import com.ddang.dog.controller.request.CreateDogRequest;
import com.ddang.dog.controller.request.UpdateDogRequest;
import com.ddang.dog.entity.Dog;
import com.ddang.dog.entity.IsNeutered;
import com.ddang.dog.entity.MemberDog;
import com.ddang.dog.repository.DogRepository;
import com.ddang.dog.repository.MemberDogRepository;
import com.ddang.dog.service.response.DogResponse;
import com.ddang.family.entity.Family;
import com.ddang.family.repository.FamilyRepository;
import com.ddang.global.entity.Gender;
import com.ddang.member.entity.*;
import com.ddang.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@Transactional
class DogServiceImplTest extends IntegrationTestSupport {

    @Autowired
    private DogService dogService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DogRepository dogRepository;

    @Autowired
    private MemberDogRepository memberDogRepository;

    @Autowired
    private FamilyRepository familyRepository;

    @BeforeEach
    void createMember(){
        Member memberHasNoDog = Member.builder()
                .name("test")
                .email("test@naver.com")
                .role(Role.USER)
                .birthDate(LocalDate.of(1999,9,3))
                .isMatched(IsMatched.TRUE)
                .address("testAddress")
                .gender(Gender.MALE)
                .familyRole(FamilyRole.ELDER_BROTHER)
                .provider(Provider.KAKAO)
                .profileImg("")
                .build();

        Family family = Family.create();

        familyRepository.save(family);

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

        Member memberHasDog = Member.builder()
                .name("test2")
                .email("test2@naver.com")
                .role(Role.USER)
                .isMatched(IsMatched.TRUE)
                .address("test2Address")
                .birthDate(LocalDate.of(2000,5,2))
                .gender(Gender.FEMALE)
                .familyRole(FamilyRole.ELDER_SISTER)
                .family(family)
                .provider(Provider.GOOGLE)
                .profileImg("")
                .build();

        memberRepository.saveAll(Arrays.asList(memberHasNoDog, memberHasDog));
        dogRepository.save(dog);

        MemberDog memberDog = MemberDog.builder()
                .dog(dog)
                .member(memberHasDog)
                .build();

        memberDogRepository.save(memberDog);
    }


    @Test
    @DisplayName("처음 강아지를 생성한다.")
    void createDogWithNoFamily() throws IOException {
        //given
        Member member = memberRepository.findByEmail("test@naver.com").get();
        CreateDogRequest request = new CreateDogRequest("banana", "sigol",
                LocalDate.of(2022,5,1),
                BigDecimal.valueOf(3.2),
                Gender.MALE,
                IsNeutered.TRUE,
                "kind");


        //when
        DogResponse response = dogService.createDog(request.toServiceRequest() ,member,null);
        Dog dog = dogRepository.findById(response.dogId()).get();

        //then
        assertThat(memberDogRepository.findAllByMember(member.getMemberId())).hasSize(1)
                .extracting("dog", "member")
                .containsExactlyInAnyOrder(
                        tuple(dog, member)
                );
    }

    @Test
    @DisplayName("처음 이후 강아지를 생성한다.")
    void createDogWithFamily() throws IOException {
        //given
        Member member = memberRepository.findByEmail("test2@naver.com").get();
        CreateDogRequest request = new CreateDogRequest("banana", "sigol",
                LocalDate.of(2022,5,1),
                BigDecimal.valueOf(3.2),
                Gender.MALE,
                IsNeutered.TRUE,
                "kind");


        //when
        dogService.createDog(request.toServiceRequest() ,member,null);
        List<Dog> dogs = dogRepository.findAll();


        //then
        assertThat(memberDogRepository.findAllByMember(member.getMemberId())).hasSize(2)
                .extracting("dog", "member")
                .containsExactlyInAnyOrder(
                        tuple(dogs.get(0), member),
                        tuple(dogs.get(1), member)
                );
    }

    @Test
    @DisplayName("강아지를 강아지 id 를 통해 조회한다.")
    void getDogByDogId() {
        //given
        Dog dog = dogRepository.findAll().get(0);

        //when
        DogResponse response = dogService.getDogByDogId(dog.getDogId());

        //then
        assertThat(response)
                .extracting("dogId" ,"dogName", "breed", "dogBirthDate", "weight", "dogGender", "isNeutered", "walkCount", "familyId", "comment")
                .containsExactlyInAnyOrder(
                        dog.getDogId(), dog.getName(), dog.getBreed(),
                        dog.getBirthDate(), dog.getWeight(), dog.getGender(),
                        dog.getIsNeutered(), dog.getWalkCount(), dog.getFamily().getFamilyId(),
                        dog.getComment()
                );
    }

    @Test
    @DisplayName("강아지 정보를 수정한다.")
    void updateDog() throws IOException {
        //given
        Member member = memberRepository.findByEmail("test2@naver.com").get();
        Dog dog = memberDogRepository.findAllByMember(member.getMemberId()).get(0).getDog();
        UpdateDogRequest request = new UpdateDogRequest("banana", null,
                null, BigDecimal.valueOf(4.5), null, null,
                "how kind of you");

        //when
        DogResponse response = dogService.updateDog(request.toServiceRequest(), dog.getDogId(), member, null);

        //then
        assertThat(response)
                .extracting("dogName", "weight", "comment")
                .containsExactlyInAnyOrder(
                   "banana", BigDecimal.valueOf(4.5), "how kind of you"
                );
    }

    @Test
    @DisplayName("강아지를 삭제한다.")
    void deleteDog() {
        //given
        Member member = memberRepository.findByEmail("test2@naver.com").get();
        Dog dog = memberDogRepository.findAllByMember(member.getMemberId()).get(0).getDog();

        Dog dog2 = Dog.builder()
                .name("choco")
                .gender(Gender.MALE)
                .profileImg("url")
                .birthDate(LocalDate.of(2023,5,7))
                .breed("sigol")
                .family(member.getFamily())
                .weight(BigDecimal.valueOf(3.7))
                .comment("kind")
                .isNeutered(IsNeutered.TRUE)
                .build();

        dogRepository.save(dog2);

        MemberDog memberDog = MemberDog.builder()
                .dog(dog2)
                .member(member)
                .build();

        memberDogRepository.save(memberDog);

        //when
        dogService.deleteDog(dog.getDogId(), member);

        //then
        assertThat(dogRepository.findActiveById(dog.getDogId())).isEmpty();
    }

    @Test
    @DisplayName("로그인한 멤버의 강아지들을 조회한다.")
    void getDogsByMember() {
        //given
        Member member = memberRepository.findByEmail("test2@naver.com").get();
        Dog dog = memberDogRepository.findAllByMember(member.getMemberId()).get(0).getDog();

        //when
        List<DogResponse> responses = dogService.getDogsByMember(member);

        //then
        assertThat(responses).hasSize(1)
                .extracting("dogId" ,"dogName", "breed", "dogBirthDate", "weight", "dogGender", "isNeutered", "walkCount", "familyId", "comment")
                .containsExactlyInAnyOrder(
                        tuple(dog.getDogId(), dog.getName(), dog.getBreed(),
                                dog.getBirthDate(), dog.getWeight(), dog.getGender(),
                                dog.getIsNeutered(), dog.getWalkCount(), dog.getFamily().getFamilyId(),
                                dog.getComment())
                );
    }
}