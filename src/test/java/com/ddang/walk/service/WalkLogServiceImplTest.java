package com.ddang.walk.service;

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
import com.ddang.walk.repository.WalkDogRepository;
import com.ddang.walk.repository.WalkRepository;
import com.ddang.walk.service.response.log.WalkLogByFamilyResponse;
import com.ddang.walk.service.response.log.WalkLogResponse;
import com.ddang.walk.service.response.log.WalkStaticsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@Transactional
class WalkLogServiceImplTest extends IntegrationTestSupport {
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private FamilyRepository familyRepository;
    
    @Autowired
    private DogRepository dogRepository;
    
    @Autowired
    private MemberDogRepository memberDogRepository;

    @Autowired
    private WalkRepository walkRepository;

    @Autowired
    private WalkDogRepository walkDogRepository;
    
    @Autowired
    private WalkLogService walkLogService;

    @BeforeEach
    void createMember(){
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

        memberRepository.save(memberHasDog);
        dogRepository.save(dog);

        MemberDog memberDog = MemberDog.builder()
                .dog(dog)
                .member(memberHasDog)
                .build();

        memberDogRepository.save(memberDog);
    }

    @Test
    @DisplayName("멤버와 강아지가 산책한 날짜를 조회합니다.")
    void getWalkLogs() {
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
        List<LocalDate> response = walkLogService.getWalkLogs(member, dog.getDogId());

        //then
        assertThat(response).hasSize(1)
                .containsExactlyInAnyOrder(LocalDate.of(2025,1,21));
    }

    @Test
    @DisplayName("날짜를 기준으로 상세 정보를 조회합니다.")
    void getWalkLogByDate() {

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
        List<WalkLogResponse> response = walkLogService.getWalkLogByDate(member, LocalDate.of(2025, 1, 21), dog.getDogId());

        //then
        assertThat(response).hasSize(1)
                .extracting("walkImg", "totalCalorie", "totalDistanceMeter")
                .containsExactlyInAnyOrder(
                        tuple("image", 300, 3000)
                );
    }

    @Test
    @DisplayName("올 한 해 강아지 산책 시킨 횟수를 조회합니다.")
    void getYearlyWalkLog() {
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
        List<Integer> response = walkLogService.getYearlyWalkLog(member, dog.getDogId());

        //then
        assertThat(response).hasSize(12)
                .containsExactlyInAnyOrder(
                        1,0,0,0,
                        0,0,0,0,
                        0,0,0,0
                );
    }

    @Test
    @DisplayName("가족들이 한 해 산책한 횟수를 조회합니다.")
    void getYearlyWalkLogByFamily() {
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
        List<WalkLogByFamilyResponse> response = walkLogService.getYearlyWalkLogByFamily(member, dog.getDogId());

        //then
        assertThat(response).hasSize(1)
                .extracting("familyRole", "memberName", "count")
                .containsExactlyInAnyOrder(
                        tuple(FamilyRole.ELDER_SISTER, "test2", 1)
                );
    }

    @Test
    @DisplayName("모든 산책 정보를 조회합니다.")
    void getTotalWalkLog() {
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
        WalkStaticsResponse response = walkLogService.getTotalWalkLog(member, dog.getDogId());

        //then
        assertThat(response).extracting("walkCount", "totalDistanceMeter")
                .containsExactlyInAnyOrder(
                        1,3000
                );
    }

    @Test
    @DisplayName("이번 달 산책 내역을 조회합니다.")
    void getMonthlyTotalWalk() {
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
        WalkStaticsResponse response = walkLogService.getMonthlyTotalWalk(member, dog.getDogId());

        //then
        assertThat(response).extracting("walkCount", "totalDistanceMeter")
                .containsExactlyInAnyOrder(
                        1,3000
                );
    }
}