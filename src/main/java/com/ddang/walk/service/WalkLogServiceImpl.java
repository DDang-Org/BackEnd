package com.ddang.walk.service;

import com.ddang.dog.entity.Dog;
import com.ddang.dog.entity.MemberDog;
import com.ddang.dog.repository.DogRepository;
import com.ddang.dog.repository.MemberDogRepository;
import com.ddang.global.exception.BadRequestException;
import com.ddang.global.exception.ErrorCode;
import com.ddang.global.exception.NotFoundException;
import com.ddang.member.entity.Member;
import com.ddang.member.repository.MemberRepository;
import com.ddang.walk.entity.Walk;
import com.ddang.walk.entity.WalkDog;
import com.ddang.walk.repository.WalkDogRepository;
import com.ddang.walk.repository.WalkRepository;
import com.ddang.walk.service.response.log.WalkLogByFamilyResponse;
import com.ddang.walk.service.response.log.WalkLogResponse;
import com.ddang.walk.service.response.log.WalkStaticsResponse;
import com.ddang.walk.util.WalkCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.Position;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class WalkLogServiceImpl implements WalkLogService{

    private final MemberDogRepository memberDogRepository;
    private final MemberRepository memberRepository;
    private final WalkDogRepository walkDogRepository;
    private final WalkRepository walkRepository;
    private final DogRepository dogRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LocalDate> getWalkLogs(Member member, Long dogId) {
        isMemberDog(member, dogId);
        List<WalkDog> walkList = walkDogRepository.findAllByDog_DogId(dogId);

        return walkList.stream().map(walk -> walk.getWalk().getStartTime().toLocalDate()).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalkLogResponse> getWalkLogByDate(Member member, LocalDate date, Long dogId) {
        isMemberDog(member, dogId);
        List<Member> memberList = memberRepository.findAllByFamily(member.getFamily());
        List<Walk> walks = walkRepository.findAllByMembersAndDate(memberList, date);
        Dog dog = getDogFromDogId(dogId);

        return walks.stream().map(walk ->
                        WalkLogResponse.of( walk, WalkCalculator.calculateCalorie(dog.getWeight(), walk.getTotalDistance()) ))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Integer> getYearlyWalkLog(Member member, Long dogId) {
        isMemberDog(member, dogId);
        List<WalkDog> walkDogs = walkDogRepository.findWalkDogsByYearAndDogId(Year.now().getValue(), dogId);
        List<Integer> list = new ArrayList<>(Collections.nCopies(12,0));

        for(WalkDog walkDog : walkDogs){
            int index = walkDog.getCreatedAt().getMonthValue()-1;
            list.set(index, list.get(index)+1);
        }

        return list;
    }

    @Transactional(readOnly = true)
    @Override
    public List<WalkLogByFamilyResponse> getYearlyWalkLogByFamily(Member member, Long dogId) {
        isMemberDog(member, dogId);
        List<Walk> walkList = walkDogRepository.findWalksByDogId(dogId, Year.now().getValue());

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

    @Transactional(readOnly = true)
    @Override
    public WalkStaticsResponse getTotalWalkLog(Member member, Long dogId) {
        List<Walk> walkList = walkDogRepository.findWalksByDogIdFromMemberId(member.getMemberId(), dogId);
        long totalSeconds = 0;
        int totalDistanceMeter = 0;
        int totalWalkCount = walkList.size();
        for(Walk walk : walkList){
            totalSeconds += ChronoUnit.SECONDS.between(walk.getStartTime(), walk.getEndTime());
            totalDistanceMeter += walk.getTotalDistance();
        }

        return WalkStaticsResponse.of(totalSeconds, totalWalkCount, totalDistanceMeter/1000);
    }

    @Transactional(readOnly = true)
    @Override
    public WalkStaticsResponse getMonthlyTotalWalk(Member member, Long dogId) {
        List<Walk> walkList = walkDogRepository.findWalksByDogIdAndMonthFromMemberId(member.getMemberId(), dogId, LocalDate.now().getMonthValue());
        long totalSeconds = 0;
        int totalDistanceMeter = 0;
        int totalWalkCount = walkList.size();
        for(Walk walk : walkList){
            totalSeconds += ChronoUnit.SECONDS.between(walk.getStartTime(), walk.getEndTime());
            totalDistanceMeter += walk.getTotalDistance();
        }

        return WalkStaticsResponse.of(totalSeconds, totalWalkCount, totalDistanceMeter/1000);
    }

    private Dog getDogFromDogId(Long dogId){
        return dogRepository.findActiveById(dogId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.DOG_NOT_FOUND));
    }

    private void isMemberDog(Member member, Long dogId){
        if(memberDogRepository.existsByMemberAndDog(member.getMemberId(), dogId) == 0){
            throw new BadRequestException(ErrorCode.NOT_MEMBER_DOG);
        }

    }

}
