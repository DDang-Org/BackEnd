package com.ddang.walk.service;

import com.ddang.dog.entity.Dog;
import com.ddang.dog.entity.MemberDog;
import com.ddang.dog.repository.DogRepository;
import com.ddang.dog.repository.MemberDogRepository;
import com.ddang.dog.service.response.DogResponse;
import com.ddang.global.exception.ErrorCode;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.ddang.global.exception.ErrorCode.DOG_NOT_FOUND;
import static com.ddang.global.service.RedisKey.*;
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
    private final DogRepository dogRepository;

    @Override
    public void startWalk(Member member, List<Long> dogIds) {
        Dog dog = getDogFromDogIdAndMemberId(dogIds.get(0), member.getMemberId());
        DogResponse dogResponse = DogResponse.from(dog);
        redisService.writeHash(WALK_DOG_KEY + member.getEmail(), dogResponse); // redis 에 response 객체 저장
        dogIds.stream().forEach(dogId -> redisService.setListValues(WALK_DOG_LIST_KEY + member.getEmail(), dogId)); // redis 에 id 를 list 형태로 저장
    }

    @Override
    @Transactional
    public CompleteWalkResponse completeWalk(Member member, CompleteWalkServiceRequest completeWalkServiceRequest,
                                             MultipartFile walkImgFile) throws IOException {
        List<Long> dogIds = getDogIdsFromRedis(member.getEmail());
        List<Dog> dogs = getDogsByDogIds(dogIds);

        String walkImg = s3Service.upload(walkImgFile, WALK_ROUTE_DIR);
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusSeconds(completeWalkServiceRequest.totalWalkTime());

        Walk walk = completeWalkServiceRequest.toEntity(startTime, endTime, member, walkImg);
        saveWalkAndDogs(walk, dogs);

        deleteAllWalkInfoFromRedis(member.getEmail());

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

    private Dog getDogFromDogIdAndMemberId(Long dogId, Long memberId){
        return memberDogRepository.findByDogIdAndMemberId(dogId, memberId)
                .orElseThrow(() -> new NotFoundException(DOG_NOT_FOUND)).getDog();
    }

    private List<Long> getDogIdsFromRedis(String email){
        List<Long> dogIds = redisService.getLongListOpsValues(WALK_DOG_LIST_KEY + email);
        if(dogIds.isEmpty()){
            throw new NotFoundException(ErrorCode.NOT_FOUND_WALKING_DOG);
        }

        return dogIds;
    }

    private List<Dog> getDogsByDogIds(List<Long> dogIds){
        List<Dog> dogs = dogRepository.findDogsByDogIds(dogIds);

        if(dogs.isEmpty()){
            throw new NotFoundException(DOG_NOT_FOUND);
        }

        return dogs;
    }

    private WalkWithDogInfo getWalkingFriendInfo(Member member){
        String key = WALK_WITH_KEY + member.getEmail();
        if(redisService.checkHasKey(key)){
            String otherEmail = redisService.getValues(key);

            MemberDog otherMemberDog = memberDogRepository.findMemberDogByMemberEmail(otherEmail, PageRequest.of(0, 1)).stream().findFirst()
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

    private void deleteAllWalkInfoFromRedis(String email){
        redisService.deleteGeoValues(POINT_KEY, email);
        redisService.deleteValues(WALK_DOG_KEY + email);
        redisService.deleteValues(WALK_DOG_LIST_KEY + email);

    }
}
