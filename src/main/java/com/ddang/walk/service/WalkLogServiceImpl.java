package com.ddang.walk.service;

import com.ddang.dog.entity.Dog;
import com.ddang.dog.repository.DogRepository;
import com.ddang.dog.repository.MemberDogRepository;
import com.ddang.family.entity.Family;
import com.ddang.global.exception.BadRequestException;
import com.ddang.global.exception.ErrorCode;
import com.ddang.member.entity.Member;
import com.ddang.member.repository.MemberRepository;
import com.ddang.walk.entity.Walk;
import com.ddang.walk.entity.WalkDog;
import com.ddang.walk.repository.WalkDogRepository;
import com.ddang.walk.service.response.log.WalkLogByFamilyResponse;
import com.ddang.walk.service.response.log.WalkLogResponse;
import com.ddang.walk.service.response.log.WalkStaticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalkLogServiceImpl implements WalkLogService{

    private final MemberDogRepository memberDogRepository;
    private final MemberRepository memberRepository;
    private final WalkDogRepository walkDogRepository;
    private final DogRepository dogRepository;

    @Override
    public List<LocalDate> getWalkLogs(Member member, Long dogId) {
        isMemberDog(member, dogId);
        List<WalkDog> walkList = walkDogRepository.findAllByDog_DogId(dogId);

        return walkList.stream().map(walk -> walk.getWalk().getStartTime().toLocalDate()).toList();
    }

    @Override
    public List<WalkLogResponse> getWalkLogByDate(Member member, LocalDate date, Long dogId) {
        isMemberDog(member, dogId);
        List<Member> memberList = memberRepository.findAllByFamily(member.getFamily());
        List<Walk> walks = walkDogRepository.findAllByMembersAndDateAndDogId(memberList, date, dogId);

        return walks.stream().map(WalkLogResponse::from)
                .toList();
    }

    @Override
    public List<Integer> getYearlyWalkLog(Member member) {
        LocalDateTime startYear = getStartYearMonth();
        List<Dog> dogs = getDogsByFamily(member.getFamily());
        List<WalkDog> walkDogs = walkDogRepository.findWalkDogsByYearAndDogs(dogs, startYear, LocalDateTime.now());
        List<Integer> list = new ArrayList<>(Collections.nCopies(12,0));

        for(WalkDog walkDog : walkDogs){
            int index = walkDog.getCreatedAt().getMonthValue()-1;
            list.set(index, list.get(index)+1);
        }

        return list;
    }


    @Override
    public List<WalkLogByFamilyResponse> getYearlyWalkLogByFamily(Member member) {
        LocalDateTime startYear = getStartYearMonth();
        List<Dog> dogs = getDogsByFamily(member.getFamily());
        List<Walk> walkList = walkDogRepository.findWalksByDogsAndYear(dogs, startYear, LocalDateTime.now());

        Map<Member, Long> walkCountByMember = walkList.stream()
                .collect(Collectors.groupingBy(Walk::getMember, Collectors.counting()));

        List<WalkLogByFamilyResponse> responseList = new ArrayList<>(walkCountByMember.entrySet().stream()
                .map(entry -> WalkLogByFamilyResponse.of(
                        entry.getKey(),
                        entry.getValue().intValue()))
                .sorted(Comparator.comparingInt(WalkLogByFamilyResponse::count).reversed())
                .toList());

        // 로그인한 유저를 맨 앞에 배치
        responseList.sort(Comparator.comparing(response -> response.memberId().equals(member.getMemberId()) ? -1 : 1));

        return responseList;
    }

    @Override
    public WalkStaticsResponse getTotalWalkLog(Member member) {
        List<Dog> dogs = getDogsByFamily(member.getFamily());
        List<Walk> walkList = walkDogRepository.findWalksByDogs(dogs);

        long totalSeconds = 0;
        int totalDistanceMeter = 0;
        int totalWalkCount = walkList.size();
        for(Walk walk : walkList){
            totalSeconds += ChronoUnit.SECONDS.between(walk.getStartTime(), walk.getEndTime());
            totalDistanceMeter += walk.getTotalDistance();
        }

        return WalkStaticsResponse.of(totalSeconds, totalWalkCount, totalDistanceMeter);
    }

    @Override
    public WalkStaticsResponse getMonthlyTotalWalk(Member member) {
        LocalDateTime startMonth = getStartYearMonth();
        List<Dog> dogs = getDogsByFamily(member.getFamily());
        List<Walk> walkList = walkDogRepository.findWalksByDogsAndMonth(dogs, startMonth, LocalDateTime.now());

        long totalSeconds = 0;
        int totalDistanceMeter = 0;
        int totalWalkCount = walkList.size();
        for(Walk walk : walkList){
            totalSeconds += ChronoUnit.SECONDS.between(walk.getStartTime(), walk.getEndTime());
            totalDistanceMeter += walk.getTotalDistance();
        }

        return WalkStaticsResponse.of(totalSeconds, totalWalkCount, totalDistanceMeter);
    }

    private void isMemberDog(Member member, Long dogId){
        if(memberDogRepository.existsByMemberAndDog(member.getMemberId(), dogId) == 0){
            throw new BadRequestException(ErrorCode.NOT_MEMBER_DOG);
        }

    }

    private LocalDateTime getStartYearMonth(){
        return LocalDateTime.of(Year.now().getValue(), 1,1,0,0,0);
    }

    private List<Dog> getDogsByFamily(Family family){
        List<Dog> dogs = dogRepository.findDogsByFamily(family);
        if(dogs.isEmpty()){
            throw new BadRequestException(ErrorCode.DOG_NOT_FOUND);
        }
        return dogs;
    }

}
