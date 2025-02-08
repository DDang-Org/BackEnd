package com.ddang.walk.repository;

import com.ddang.IntegrationTestSupport;
import com.ddang.dog.entity.Dog;
import com.ddang.dog.entity.IsNeutered;
import com.ddang.dog.entity.MemberDog;
import com.ddang.dog.repository.DogRepository;
import com.ddang.dog.repository.MemberDogRepository;
import com.ddang.family.entity.Family;
import com.ddang.family.repository.FamilyRepository;
import com.ddang.global.entity.Gender;
import com.ddang.member.entity.*;
import com.ddang.member.repository.MemberRepository;
import com.ddang.walk.entity.Walk;
import com.ddang.walk.entity.WalkDog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Transactional
class WalkDogRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private WalkDogRepository walkDogRepository;

    @Autowired
    private WalkRepository walkRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private DogRepository dogRepository;

    @Autowired
    private MemberDogRepository memberDogRepository;

    @BeforeEach
    void createMember(){

        Member memberHasDog = Member.builder()
                .name("test2")
                .email("test2@naver.com")
                .role(Role.USER)
                .isMatched(IsMatched.TRUE)
                .address("test2Address")
                .birthDate(LocalDate.of(2000,5,2))
                .gender(Gender.FEMALE)
                .familyRole(FamilyRole.ELDER_SISTER)
                .family(null)
                .provider(Provider.GOOGLE)
                .profileImg(1)
                .build();

        memberRepository.save(memberHasDog);

        Family family = Family.create(memberHasDog.getMemberId());
        familyRepository.save(family);
        memberHasDog.updateFamily(family);

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

        MemberDog memberDog = MemberDog.builder()
                .dog(dog)
                .member(memberHasDog)
                .build();

        memberDogRepository.save(memberDog);
    }

    @Test
    @DisplayName("dogID 로 WalkDog 을 모두 조회합니다.")
    void findAllByDog_DogId() {
        //given
        Member member = memberRepository.findByEmail("test2@naver.com").get();
        Dog dog = memberDogRepository.findAllByMember(member.getMemberId()).get(0).getDog();

        Walk walk = Walk.builder()
                .walkImg("image")
                .member(member)
                .startTime(LocalDateTime.of(2025,1,21,9,30,0))
                .endTime(LocalDateTime.of(2025,1,21,11,0,0))
                .totalCalorie(300)
                .totalDistance(3000)
                .build();

        walkRepository.save(walk);

        WalkDog walkDog = WalkDog.builder()
                .dog(dog)
                .walk(walk)
                .build();

        walkDogRepository.save(walkDog);

        //when
        List<WalkDog> response = walkDogRepository.findAllByDog_DogId(dog.getDogId());

        //then
        assertThat(response).hasSize(1)
                .extracting("walk", "dog")
                .containsExactlyInAnyOrder(
                        tuple(walk, dog)
                );

    }

    @Test
    @DisplayName("년도와 DogId로 WalkDog 을 조회한다.")
    void findWalkDogsByYearAndDogId() {
        //given
        Member member = memberRepository.findByEmail("test2@naver.com").get();
        Dog dog = memberDogRepository.findAllByMember(member.getMemberId()).get(0).getDog();
        List<Dog> dogs = List.of(dog);

        Walk walk = Walk.builder()
                .walkImg("image")
                .member(member)
                .startTime(LocalDateTime.of(2025,1,21,9,30,0))
                .endTime(LocalDateTime.of(2025,1,21,11,0,0))
                .totalCalorie(300)
                .totalDistance(3000)
                .build();

        walkRepository.save(walk);

        WalkDog walkDog = WalkDog.builder()
                .dog(dog)
                .walk(walk)
                .build();

        walkDogRepository.save(walkDog);

        //when
        LocalDateTime startYear = getStartYearMonth();
        LocalDateTime now = LocalDateTime.now();
        List<WalkDog> response = walkDogRepository.findWalkDogsByYearAndDogs(dogs, startYear, now);

        //then
        assertThat(response).hasSize(1)
                .extracting("walk", "dog")
                .containsExactlyInAnyOrder(
                        tuple(walk, dog)
                );
    }

    @Test
    @DisplayName("DogId를 통해 Walks 를 조회한다.")
    void findWalksByDogId() {
        //given
        Member member = memberRepository.findByEmail("test2@naver.com").get();
        Dog dog = memberDogRepository.findAllByMember(member.getMemberId()).get(0).getDog();
        List<Dog> dogs = List.of(dog);

        Walk walk = Walk.builder()
                .walkImg("image")
                .member(member)
                .startTime(LocalDateTime.of(2025,1,21,9,30,0))
                .endTime(LocalDateTime.of(2025,1,21,11,0,0))
                .totalCalorie(300)
                .totalDistance(3000)
                .build();

        walkRepository.save(walk);

        WalkDog walkDog = WalkDog.builder()
                .dog(dog)
                .walk(walk)
                .build();

        walkDogRepository.save(walkDog);

        //when
        List<Walk> response = walkDogRepository.findWalksByDogs(dogs);

        //then
        assertThat(response).hasSize(1)
                .extracting("walkImg", "member", "startTime", "endTime", "totalCalorie", "totalDistance")
                .containsExactlyInAnyOrder(
                        tuple("image", member, LocalDateTime.of(2025,1,21,9,30,0),
                        LocalDateTime.of(2025,1,21,11,0,0), 300, 3000)
                );

    }

    @Test
    @DisplayName("dogId 와 Month 를 기준으로 Walks를 조회한다.")
    void findWalksByDogIdAndMonth() {
        //given
        Member member = memberRepository.findByEmail("test2@naver.com").get();
        Dog dog = memberDogRepository.findAllByMember(member.getMemberId()).get(0).getDog();
        List<Dog> dogs = List.of(dog);

        Walk walk = Walk.builder()
                .walkImg("image")
                .member(member)
                .startTime(LocalDateTime.of(2025,1,21,9,30,0))
                .endTime(LocalDateTime.of(2025,1,21,11,0,0))
                .totalCalorie(300)
                .totalDistance(3000)
                .build();

        walkRepository.save(walk);

        WalkDog walkDog = WalkDog.builder()
                .dog(dog)
                .walk(walk)
                .build();

        walkDogRepository.save(walkDog);

        //when
        LocalDateTime startMonth = getStartMonth();
        LocalDateTime now = LocalDateTime.now();
        List<Walk> response = walkDogRepository.findWalksByDogsAndMonth(dogs, startMonth, now);

        //then
        assertThat(response).hasSize(1)
                .extracting("walkImg", "member", "startTime", "endTime", "totalCalorie", "totalDistance")
                .containsExactlyInAnyOrder(
                        tuple("image", member, LocalDateTime.of(2025,1,21,9,30,0),
                                LocalDateTime.of(2025,1,21,11,0,0), 300, 3000)
                );
    }


    @Test
    @DisplayName("dogId 를 통해 오늘의 산책 정보를 조회한다.")
    void findWalksByDogIdAndToday() {
        //given
        Member member = memberRepository.findByEmail("test2@naver.com").get();
        Dog dog = memberDogRepository.findAllByMember(member.getMemberId()).get(0).getDog();

        Walk walk = Walk.builder()
                .walkImg("image")
                .member(member)
                .startTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(9,0)))
                .endTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(11,0)))
                .totalCalorie(300)
                .totalDistance(3000)
                .build();

        walkRepository.save(walk);

        WalkDog walkDog = WalkDog.builder()
                .dog(dog)
                .walk(walk)
                .build();

        walkDogRepository.save(walkDog);

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay().minusNanos(1);

        //when
        List<Walk> response = walkDogRepository.findTodayWalksByDogId(dog.getDogId(), startOfDay, endOfDay);

        //then
        assertThat(response).hasSize(1)
                .extracting("walkImg", "member", "startTime", "endTime", "totalCalorie", "totalDistance")
                .containsExactlyInAnyOrder(
                        tuple("image", member,LocalDateTime.of(LocalDate.now(), LocalTime.of(9,0)),
                                LocalDateTime.of(LocalDate.now(), LocalTime.of(11,0)), 300, 3000)
                );
    }

    @Test
    @DisplayName("다양한 사용자와 날짜 그리고 dogId 를 통해 Walks 를 조회한다.")
    void findAllByMembersAndDateAndDogId() {
        //given
        Member member = memberRepository.findByEmail("test2@naver.com").get();
        Dog dog = memberDogRepository.findAllByMember(member.getMemberId()).get(0).getDog();

        Walk walk = Walk.builder()
                .walkImg("image")
                .member(member)
                .startTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(9,0)))
                .endTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(11,0)))
                .totalCalorie(300)
                .totalDistance(3000)
                .build();

        walkRepository.save(walk);

        WalkDog walkDog = WalkDog.builder()
                .dog(dog)
                .walk(walk)
                .build();

        walkDogRepository.save(walkDog);
        List<Member> members = new ArrayList<>();
        members.add(member);

        //when
        List<Walk> response = walkDogRepository.findAllByMembersAndDateAndDogId(members ,LocalDate.now(),dog.getDogId());

        //then
        assertThat(response).hasSize(1)
                .extracting("walkImg", "member", "startTime", "endTime", "totalCalorie", "totalDistance")
                .containsExactlyInAnyOrder(
                        tuple("image", member,LocalDateTime.of(LocalDate.now(), LocalTime.of(9,0)),
                                LocalDateTime.of(LocalDate.now(), LocalTime.of(11,0)), 300, 3000)
                );
    }

    private LocalDateTime getStartMonth(){
        return LocalDateTime.of(Year.now().getValue(), LocalDateTime.now().getMonthValue(), 1,0,0,0);
    }

    private LocalDateTime getStartYearMonth(){
        return LocalDateTime.of(Year.now().getValue(), 1,1,0,0,0);
    }
}