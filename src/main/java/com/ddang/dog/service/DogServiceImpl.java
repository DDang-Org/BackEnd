package com.ddang.dog.service;

import com.ddang.dog.entity.Dog;
import com.ddang.dog.entity.MemberDog;
import com.ddang.dog.repository.DogRepository;
import com.ddang.dog.repository.MemberDogRepository;
import com.ddang.dog.service.request.CreateDogServiceRequest;
import com.ddang.dog.service.request.UpdateDogServiceRequest;
import com.ddang.dog.service.response.DogResponse;
import com.ddang.dog.service.response.DogWalkResponse;
import com.ddang.family.entity.Family;
import com.ddang.family.repository.FamilyRepository;
import com.ddang.global.exception.BadRequestException;
import com.ddang.global.exception.ErrorCode;
import com.ddang.global.service.S3Service;
import com.ddang.member.entity.Member;
import com.ddang.member.repository.MemberRepository;
import com.ddang.walk.entity.Walk;
import com.ddang.walk.repository.WalkDogRepository;
import com.ddang.walk.repository.WalkRepository;
import com.ddang.walk.service.response.TimeDuration;
import com.ddang.walk.util.WalkCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class DogServiceImpl implements DogService {

    private final DogRepository dogRepository;
    private final MemberRepository memberRepository;
    private final MemberDogRepository memberDogRepository;
    private final FamilyRepository familyRepository;
    private final WalkDogRepository walkDogRepository;
    private final S3Service s3Service;

    private final static String DOG_PROFILE_DIR = "dog";
    private final static Integer MAX_DOG = 5;

    @Transactional
    public DogResponse createDog(CreateDogServiceRequest request, Member member, MultipartFile profileImgFile) throws IOException {
        validateRepresentativeMember(member);

        throwIfExceedsMaxLimit(member);

        String profileImg = getProfileImgUrlOrElseGetNull(profileImgFile);
        createFamilyIfNotExists(member);
        Dog dog = request.toEntity(profileImg, member.getFamily());
        List<MemberDog> memberDog = assignDogToFamilyMembers(member, dog);

        dogRepository.save(dog);
        memberDogRepository.saveAll(memberDog);

        return DogResponse.from(dog);
    }

    public DogResponse getDogByDogId(Long dogId) {
        Dog dog = findDogByIdOrThrowException(dogId);

        return DogResponse.from(dog);
    }

    @Transactional
    public DogResponse updateDog(UpdateDogServiceRequest request, Long dogId, Member member, MultipartFile profileImgFile) throws IOException {
        validateRepresentativeMember(member);


        MemberDog memberDog = memberDogRepository.findByDogIdAndMemberId(dogId, member.getMemberId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.MEMBER_NOT_HAVE_DOG));

        Dog dog = memberDog.getDog();
        String profileImg = getProfileImgUrlOrElseGetNull(profileImgFile);

        dog.update(request, profileImg);
        return DogResponse.from(dog);
    }

    @Transactional
    public void deleteDog(Long dogId, Member member) {
        validateRepresentativeMember(member);

        throwIfOnlyOneDogExists(member);

        memberDogRepository.softDeleteByDogId(dogId);

        dogRepository.softDeleteById(dogId);
    }

    public List<DogResponse> getDogsByMember(Long memberId) {
        List<MemberDog> memberDogs = memberDogRepository.findAllByMember(memberId);

        return memberDogs.stream()
                .map(memberDog -> DogResponse.from(memberDog.getDog()))
                .toList();
    }

    @Override
    public DogWalkResponse dogWalk(Member member, Long dogId) {
        Map<String, Long> walkSummary = calculateWalkSummary(dogId);

        long totalDistanceMeter = walkSummary.get("totalDistanceMeter");
        TimeDuration timeDuration = TimeDuration.from(walkSummary.get("totalSeconds"));
        int totalCalorie = walkSummary.get("totalCalorie").intValue();

        return DogWalkResponse.of(timeDuration, totalDistanceMeter, totalCalorie);
    }

    private Map<String, Long> calculateWalkSummary(Long dogId) {
        long totalSeconds = 0;
        long totalDistanceMeter = 0;
        long totalCalorie = 0;
        List<Walk> walkList = findTodayWalksByDogId(dogId);

        for (Walk walk : walkList) {
            totalCalorie += walk.getTotalCalorie();
            totalSeconds += ChronoUnit.SECONDS.between(walk.getStartTime(), walk.getEndTime());
            totalDistanceMeter += walk.getTotalDistance();
        }

        Map<String, Long> summary = new HashMap<>();
        summary.put("totalCalorie", totalCalorie);
        summary.put("totalSeconds", totalSeconds);
        summary.put("totalDistanceMeter", totalDistanceMeter);
        return summary;
    }

    private List<Walk> findTodayWalksByDogId(Long dogId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay().minusNanos(1);
        return walkDogRepository.findTodayWalksByDogId(dogId, startOfDay, endOfDay);
    }


    private Dog findDogByIdOrThrowException(Long id) {
        return dogRepository.findActiveById(id)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", id, ErrorCode.DOG_NOT_FOUND);
                    return new BadRequestException(ErrorCode.DOG_NOT_FOUND);
                });
    }

    private String getProfileImgUrlOrElseGetNull(MultipartFile profileImgFile) throws IOException {
        return s3Service.upload(profileImgFile, DOG_PROFILE_DIR);
    }

    private void createFamilyIfNotExists(Member member){
        if(member.hasNoFamily()) {
            Family family = Family.create(member.getMemberId());
            family = familyRepository.save(family);

            member.updateFamily(family);
            memberRepository.save(member);
        }
    }

    private List<MemberDog> assignDogToFamilyMembers(Member member, Dog dog){

        List<Member> members = memberRepository.findAllByFamily(member.getFamily());
        return members.stream()
                .map(familyMember -> MemberDog.builder()
                        .member(familyMember)
                        .dog(dog)
                        .build())
                .toList();
    }

    private void throwIfExceedsMaxLimit(Member member){
        if(memberDogRepository.countAllByMember(member) > MAX_DOG){
            throw new BadRequestException(ErrorCode.OVER_MAX_DOG);
        }
    }

    private void throwIfOnlyOneDogExists(Member member){
        if(memberDogRepository.countAllByMember(member) == 1){
            throw new BadRequestException(ErrorCode.FAMILY_MUST_HAVE_ONE_DOG);
        }
    }

    private void validateRepresentativeMember(Member member){
        if(member.isNotRepresentativeFamilyMember()){
            throw new BadRequestException(ErrorCode.MEMBER_NOT_FAMILY_BOSS);
        }
    }


}


