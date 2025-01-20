package com.ddang.dog.controller;

import com.ddang.dog.controller.request.CreateDogRequest;
import com.ddang.dog.controller.request.UpdateDogRequest;
import com.ddang.dog.service.DogService;
import com.ddang.dog.service.response.DogResponse;
import com.ddang.global.api.ApiResponse;
import com.ddang.global.exception.annotation.SwaggerExceptionResponse;
import com.ddang.member.oauth2.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.ddang.global.exception.ErrorCode.*;

@RestController
@RequestMapping("/api/v1/dogs")
@RequiredArgsConstructor
@Tag(name = "Dog API", description = "강아지 API")
public class DogController {

    private final DogService dogService;

    @PostMapping(value = "/create", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(
            summary = "반려견 등록",
            description = "반려견을 등록합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "반려견 등록 정보",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateDogRequest.class)
                    )
            ),
            parameters = {
                    @Parameter( name = "profileImgFile",
                            description = "Profile Image File",
                            schema = @Schema(type = "string", format = "binary") ) }
    )
    @SwaggerExceptionResponse({DOG_NOT_FOUND, OVER_MAX_DOG, NAME_NOT_NULL, NAME_EXCEED, BREED_NOT_NULL, DATE_MUST_BE_PAST_OR_PRESENT, WEIGHT_MINIMUM, WEIGHT_MAXIMUM,
            WEIGHT_DECIMAL_LIMIT,GENDER_REQUIRED,NEUTERING_REQUIRED,COMMENT_SIZE_EXCEED, MEMBER_NOT_FOUND, DOG_ALREADY_OWNED})
    public ApiResponse<DogResponse> createDog(
            @RequestPart @Valid CreateDogRequest request,
            @RequestPart(required = false) MultipartFile profileImgFile,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException {
        DogResponse response = dogService.createDog(request.toServiceRequest(), customOAuth2User.getMember(), profileImgFile);
        return ApiResponse.created(response);
    }

    @GetMapping("/{dogId}")
    @Operation(summary = "반려견 조회", description = "반려견을 조회합니다.")
    @SwaggerExceptionResponse({DOG_NOT_FOUND})
    public ApiResponse<DogResponse> getDogByDogId(
            @PathVariable Long dogId) {
        DogResponse response = dogService.getDogByDogId(dogId);
        return ApiResponse.ok(response);
    }

    @GetMapping("")
    @Operation(summary = "내 반려견 조회", description = "반려견을 조회합니다.")
    @SwaggerExceptionResponse({DOG_NOT_FOUND, MEMBER_NOT_FOUND})
    public ApiResponse<List<DogResponse>> getMyDogs(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        List<DogResponse> responses = dogService.getDogsByMember(customOAuth2User.getMember());
        return ApiResponse.ok(responses);
    }

    @PatchMapping(value = "/{dogId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(
            summary = "반려견 정보 수정",
            description = "반려견 정보를 수정합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "반려견 수정 정보",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateDogRequest.class)
                    )
            ),
            parameters = {
                    @Parameter( name = "profileImgFile",
                            description = "Profile Image File",
                            schema = @Schema(type = "string", format = "binary") ) }
    )
    @SwaggerExceptionResponse({DOG_NOT_FOUND, OVER_MAX_DOG, NAME_NOT_NULL, NAME_EXCEED, BREED_NOT_NULL, DATE_MUST_BE_PAST_OR_PRESENT, WEIGHT_MINIMUM, WEIGHT_MAXIMUM,
            WEIGHT_DECIMAL_LIMIT,GENDER_REQUIRED,NEUTERING_REQUIRED,COMMENT_SIZE_EXCEED, MEMBER_NOT_FOUND, DOG_ALREADY_OWNED})
    public ApiResponse<DogResponse> updateDog(
            @PathVariable Long dogId,
            @RequestPart @Valid UpdateDogRequest request,
            @RequestPart(required = false) MultipartFile profileImgFile,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException {

        DogResponse response = dogService.updateDog(request.toServiceRequest(), dogId, customOAuth2User.getMember(), profileImgFile);

        return ApiResponse.ok(response);
    }

    @DeleteMapping("/{dogId}")
    @Operation(summary = "반려견 삭제", description = "반려견을 삭제합니다.")
    @SwaggerExceptionResponse({MEMBER_NOT_FOUND, FAMILY_MUST_HAVE_ONE_DOG})
    public ApiResponse<Void> deleteDog(
            @PathVariable Long dogId,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {

        dogService.deleteDog(dogId, customOAuth2User.getMember());

        return ApiResponse.noContent();
    }
}

