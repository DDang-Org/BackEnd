package com.ddang.dog.service;
//깃허브 데스크탑
//테스트

import com.ddang.dog.entity.Dog;
import com.ddang.dog.entity.MemberDog;
import com.ddang.dog.repository.DogRepository;
import com.ddang.dog.repository.MemberDogRepository;
import com.ddang.dog.service.request.CreateDogServiceRequest;
import com.ddang.dog.service.request.UpdateDogServiceRequest;
import com.ddang.dog.service.response.DogResponse;
import com.ddang.family.entity.Family;
import com.ddang.family.repository.FamilyRepository;
import com.ddang.global.exception.BadRequestException;
import com.ddang.global.exception.ErrorCode;
import com.ddang.global.service.S3Service;
import com.ddang.member.entity.Member;
import com.ddang.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DogServiceImpl implements DogService{

    private final DogRepository dogRepository;
    private final MemberRepository memberRepository;
    private final MemberDogRepository memberDogRepository;
    private final FamilyRepository familyRepository;
    private final S3Service s3Service;

    private final static String DOG_PROFILE_DIR = "dog";
    private final static Integer MAX_DOG = 5;

    public DogResponse createDog(CreateDogServiceRequest request, Member member, MultipartFile profileImgFile) throws IOException {
        // TODO : 패밀리장인지 유효성 검사

        throwIfExceedsMaxLimit(member);

        String profileImg = s3Service.upload(profileImgFile, DOG_PROFILE_DIR);
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

    public DogResponse updateDog(UpdateDogServiceRequest request, Long dogId, Member member, MultipartFile profileImgFile) throws IOException {
        // TODO : 패밀리장인지 유효성 검사

        MemberDog memberDog = memberDogRepository.findByDogIdAndMemberId(dogId, member.getMemberId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.MEMBER_NOT_HAVE_DOG));

        Dog dog = memberDog.getDog();
        String profileImg = getProfileImgUrlOrElseGetNull(profileImgFile);

        dog.update(request, profileImg);
        return DogResponse.from(dog);
    }

    public void deleteDog(Long dogId, Member member) {
        // TODO : 패밀리장인지 유효성 검사
        throwIfOnlyOneDogExists(member);

        memberDogRepository.softDeleteByDogId(dogId);

        dogRepository.softDeleteById(dogId);
        // TODO 산책 내역 삭제하기
    }

    public List<DogResponse> getDogsByMember(Member member) {
        List<MemberDog> memberDogs = memberDogRepository.findAllByMember(member);

        return memberDogs.stream()
                .map(memberDog -> DogResponse.from(memberDog.getDog()))
                .toList();
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
            Family family = Family.create();
            family = familyRepository.save(family);

            member.updateFamily(family);
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


}


