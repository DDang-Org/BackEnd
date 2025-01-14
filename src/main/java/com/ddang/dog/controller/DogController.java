package com.ddang.dog.controller;

import com.ddang.dog.controller.request.CreateDogRequest;
import com.ddang.dog.controller.request.UpdateDogRequest;
import com.ddang.dog.service.DogService;
import com.ddang.dog.service.response.DogResponse;
import com.ddang.global.api.ApiResponse;
import com.ddang.member.oauth2.CustomOAuth2User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dogs")
@RequiredArgsConstructor
@Tag(name = "Dog API", description = "강아지 API")
public class DogController {

    private final DogService dogService;

    @PostMapping(value = "/create", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<DogResponse> createDog(
            @RequestPart @Valid CreateDogRequest request,
            @RequestPart(required = false) MultipartFile profileImgFile,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException {
        DogResponse response = dogService.createDog(request.toServiceRequest(), customOAuth2User.getMember(), profileImgFile);
        return ApiResponse.created(response);
    }

    @GetMapping("/{dogId}")
    public ApiResponse<DogResponse> getDogByDogId(
            @PathVariable Long dogId) {
        DogResponse response = dogService.getDogByDogId(dogId);
        return ApiResponse.ok(response);
    }

    @GetMapping("")
    public ApiResponse<List<DogResponse>> getMyDogs(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        List<DogResponse> responses = dogService.getDogsByMember(customOAuth2User.getMember());
        return ApiResponse.ok(responses);
    }

    @PatchMapping(value = "/{dogId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<DogResponse> updateDog(
            @PathVariable Long dogId,
            @RequestPart @Valid UpdateDogRequest request,
            @RequestPart(required = false) MultipartFile profileImgFile,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException {

        Long memberId = customOAuth2User.getMember().getMemberId();
        DogResponse response = dogService.updateDog(request.toServiceRequest(), dogId, memberId, profileImgFile);

        return ApiResponse.ok(response);
    }

    @DeleteMapping("/{dogId}")
    public ApiResponse<Void> deleteDog(
            @PathVariable Long dogId,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {

        Long memberId = customOAuth2User.getMember().getMemberId();
        dogService.deleteDog(dogId, memberId);

        return ApiResponse.noContent();
    }
}

