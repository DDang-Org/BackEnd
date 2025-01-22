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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
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
    void getWalkLogByDate() {
    }

    @Test
    void getYearlyWalkLog() {
    }

    @Test
    void getYearlyWalkLogByFamily() {
    }

    @Test
    void getTotalWalkLog() {
    }

    @Test
    void getMonthlyTotalWalk() {
    }
}