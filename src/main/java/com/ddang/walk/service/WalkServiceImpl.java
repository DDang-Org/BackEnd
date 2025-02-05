package com.ddang.walk.service;

import com.ddang.dog.entity.Dog;
import com.ddang.dog.entity.MemberDog;
import com.ddang.dog.repository.MemberDogRepository;
import com.ddang.global.exception.NotFoundException;
import com.ddang.global.service.RedisService;
import com.ddang.global.service.S3Service;
import com.ddang.member.entity.Member;
import com.ddang.member.entity.WalkWithMember;
import com.ddang.member.repository.WalkWithMemberRepository;
import com.ddang.walk.entity.Walk;
import com.ddang.walk.entity.WalkDog;
import com.ddang.walk.repository.WalkDogRepository;
import com.ddang.walk.repository.WalkRepository;
import com.ddang.walk.service.request.CompleteWalkServiceRequest;
import com.ddang.walk.service.response.walk.CompleteWalkResponse;
import com.ddang.walk.service.response.walk.WalkWithDogInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.ddang.global.exception.ErrorCode.DOG_NOT_FOUND;
import static com.ddang.global.service.RedisKey.POINT_KEY;
import static com.ddang.global.service.RedisKey.WALK_WITH_KEY;
import static com.ddang.walk.util.WalkCalculator.calculateCalorie;


@Service
@RequiredArgsConstructor
public class WalkServiceImpl implements WalkService{

    private final RedisService redisService;
    private final WalkRepository walkRepository;
    private final MemberDogRepository memberDogRepository;
    private final WalkDogRepository walkDogRepository;
    private final WalkWithMemberRepository walkWithMemberRepository;
    private final S3Service s3Service;

    private final static String WALK_ROUTE_DIR = "walk";

    @Override
    @Transactional
    public CompleteWalkResponse completeWalk(Member member, CompleteWalkServiceRequest completeWalkServiceRequest,
                                             MultipartFile walkImgFile) throws IOException {
        List<Dog> dogs = getDogsFromMemberId(member.getMemberId());
        String walkImg = s3Service.upload(walkImgFile, WALK_ROUTE_DIR);
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusSeconds(completeWalkServiceRequest.totalWalkTime());

        Walk walk = completeWalkServiceRequest.toEntity(startTime, endTime, member, walkImg);
        saveWalkAndDogs(walk, dogs);

        redisService.deleteGeoValues(POINT_KEY, member.getEmail());

        return CompleteWalkResponse.of(
                member.getName(), dogs.get(0).getName(), walk.getTotalDistance(), completeWalkServiceRequest.totalWalkTime(),
                calculateCalorie(dogs.get(0).getWeight(),completeWalkServiceRequest.totalDistance()),
                walk.getWalkImg(), getWalkingFriendInfo(member)
        );
    }

    private void saveWalkAndDogs(Walk walk, List<Dog> dogs){

        List<WalkDog> walkDogs = dogs.stream()
                .map(dog -> WalkDog.builder()
                        .dog(dog)
                        .walk(walk)
                        .build())
                .toList();

        dogs.forEach(Dog::doWalk);
        walkRepository.save(walk);
        walkDogRepository.saveAll(walkDogs);
    }

    private List<Dog> getDogsFromMemberId(Long memberId){
        List<Dog> dogs = memberDogRepository.findDogsByMemberId(memberId);

        if(dogs.isEmpty()){
            throw new NotFoundException(DOG_NOT_FOUND);
        }

        return dogs;
    }

    private WalkWithDogInfo getWalkingFriendInfo(Member member){
        String key = WALK_WITH_KEY + member.getEmail();
        if(redisService.checkHasKey(key)){
            String otherEmail = redisService.getValues(key);

            MemberDog otherMemberDog = memberDogRepository.findMemberDogByMemberEmail(otherEmail)
                    .orElseThrow(() -> new NotFoundException(DOG_NOT_FOUND));

            saveWalkWithMember(member, otherMemberDog.getMember());

            redisService.deleteValues(key);
            return WalkWithDogInfo.of(otherMemberDog.getMember(), otherMemberDog.getDog());
        }

        return null;
    }

    private void saveWalkWithMember(Member member, Member otherMember){
        WalkWithMember walkWithMember = WalkWithMember.builder()
                .sender(member)
                .receiver(otherMember)
                .build();

        walkWithMemberRepository.save(walkWithMember);
    }
}
