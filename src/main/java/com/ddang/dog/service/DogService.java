package com.ddang.dog.service;

import com.ddang.dog.service.request.CreateDogServiceRequest;
import com.ddang.dog.service.request.UpdateDogServiceRequest;
import com.ddang.dog.service.response.DogResponse;
import com.ddang.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DogService {
    DogResponse createDog(CreateDogServiceRequest request, Member member, MultipartFile profileImgFile) throws IOException;

    DogResponse getDogByDogId(Long dogId);

    DogResponse updateDog(UpdateDogServiceRequest request,Long dogId, Long memberId, MultipartFile profileImgFile) throws IOException;

    void deleteDog(Long dogId, Long memberId);

    List<DogResponse> getDogsByMember(Member member);
}
