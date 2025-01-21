package com.ddang.family.controller;

import com.ddang.dog.service.response.DogResponse;
import com.ddang.family.controller.request.FamilyJoinRequest;
import com.ddang.family.service.FamilyService;
import com.ddang.family.service.response.FamilyDogResponse;
import com.ddang.family.service.response.FamilyMemberResponse;
import com.ddang.family.service.response.FamilyResponse;
import com.ddang.family.service.response.InviteCodeResponse;
import com.ddang.global.api.ApiResponse;
import com.ddang.global.exception.annotation.SwaggerExceptionResponse;
import com.ddang.member.entity.Member;
import com.ddang.member.oauth2.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ddang.global.exception.ErrorCode.*;

@RestController
@RequestMapping("/api/v1/family")
@RequiredArgsConstructor
@Tag(name = "Family API", description = "가족 생성, 조회 및 삭제 API")
public class FamilyController {
    private final FamilyService familyService;

    @GetMapping("/invite-code")
    @Operation(
            summary = "가족 초대 코드 생성",
            description = """
                    지정된 가족에 대한 5분 유효기간의 초대 코드를 생성합니다.
                    생성된 초대 코드는 반환되며, 5분 후에 만료됩니다.
                    이미 초대 코드가 있는 경우, 남은 유효기간과 함께 초대 코드를 반환합니다.
                    """
    )
    @SwaggerExceptionResponse({FAMILY_NOT_FOUND, MEMBER_NOT_IN_FAMILY, MEMBER_NOT_FOUND})
    public ApiResponse<InviteCodeResponse> createInviteCode(@AuthenticationPrincipal CustomOAuth2User currentUser) {
        Member currentMember = currentUser.getMember();
        InviteCodeResponse response = familyService.createInviteCode(currentMember);
        return ApiResponse.created(response);
    }

    @PostMapping(value = "/dogs", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(
            summary = "초대 코드를 입력하여 해당 패밀리댕이 보유한 강아지 정보 리스트를 받습니다.",
            description = """
                    초대 코드를 입력하여 가족의 강아지 정보 리스트를 받습니다.
                    초대 코드가 유효하지 않거나 만료되었을 경우 오류를 반환합니다.
                    """
    )
    @SwaggerExceptionResponse({INVALID_INVITE_CODE})
    public ApiResponse<List<DogResponse>> getFamilyDogs(@RequestBody FamilyJoinRequest request) {
        List<DogResponse> response = familyService.getFamilyDogs(request.inviteCode());
        return ApiResponse.ok(response);
    }

    @PostMapping(value = "/join", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(
            summary = "가족에 참여",
            description = """
                    초대 코드를 입력하여 가족에 참여합니다.
                    초대 코드가 유효하지 않거나 만료되었을 경우 오류를 반환합니다.
                    """
    )
    @SwaggerExceptionResponse({MEMBER_IN_FAMILY, MEMBER_HAVE_DOG, INVALID_INVITE_CODE, FAMILY_NOT_FOUND, MEMBER_NOT_FOUND})
    public ApiResponse<FamilyResponse> joinFamily(@RequestBody FamilyJoinRequest request,
                                                  @AuthenticationPrincipal CustomOAuth2User currentUser) {
        Member currentMember = currentUser.getMember();
        FamilyResponse response = familyService.addMemberToFamily(request.inviteCode(), currentMember);
        return ApiResponse.ok(response);
    }

    @GetMapping("/my-dogs")
    @Operation(
            summary = "내 패밀리댕 강아지 목록 조회",
            description = """
                로그인한 사용자가 속한 패밀리댕 강아지 정보 목록을 조회합니다.
                """
    )
    @SwaggerExceptionResponse({FAMILY_NOT_FOUND, MEMBER_NOT_IN_FAMILY, MEMBER_NOT_FOUND})
    public ApiResponse<List<FamilyDogResponse>> getMyFamilyDogs(@AuthenticationPrincipal CustomOAuth2User currentUser) {
        Member currentMember = currentUser.getMember();
        List<FamilyDogResponse> response = familyService.getMyFamilyDogs(currentMember);
        return ApiResponse.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "내 패밀리댕 정보 조회",
            description = """
                로그인한 사용자가 속한 패밀리댕 정보를 조회합니다.
                """
    )
    @SwaggerExceptionResponse({FAMILY_NOT_FOUND, MEMBER_NOT_IN_FAMILY, MEMBER_NOT_FOUND})
    public ApiResponse<List<FamilyMemberResponse>> getMyFamily(@AuthenticationPrincipal CustomOAuth2User currentUser) {
        Member currentMember = currentUser.getMember();
        List<FamilyMemberResponse> response = familyService.getMyFamily(currentMember);
        return ApiResponse.ok(response);
    }

    @PutMapping("/representative/{memberId}")
    @Operation(
            summary = "가족 대표 위임",
            description = """
                현재 가족 대표가 다른 가족 구성원을 대표로 위임합니다.
                대표는 동일한 가족 구성원만 위임 가능합니다.
                """
    )
    @SwaggerExceptionResponse({FAMILY_NOT_FOUND, MEMBER_NOT_IN_FAMILY, MEMBER_NOT_FOUND, MEMBER_NOT_FAMILY_BOSS, INVALID_FAMILY_MEMBER})
    public ApiResponse<Void> assignRepresentative(
            @PathVariable Long memberId,
            @AuthenticationPrincipal CustomOAuth2User currentUser
    ) {
        familyService.assignFamilyRepresentative(currentUser.getMember(), memberId);
        return ApiResponse.noContent();
    }

    @DeleteMapping("/members/{memberId}")
    @Operation(
            summary = "가족 유저 추방",
            description = """
                가족 소유자가 특정 유저를 가족에서 추방합니다.
                추방 권한은 가족 소유자에게만 주어집니다.
                """
    )
    @SwaggerExceptionResponse({FAMILY_NOT_FOUND, MEMBER_NOT_IN_FAMILY, MEMBER_NOT_FOUND, MEMBER_NOT_FAMILY_BOSS, SELF_REMOVE_NOT_ALLOWED, INVALID_FAMILY_MEMBER})
    public ApiResponse<Void> removeMember(
            @PathVariable Long memberId,
            @AuthenticationPrincipal CustomOAuth2User currentUser
    ) {
        familyService.removeMemberFromFamily(memberId, currentUser.getMember());
        return ApiResponse.noContent();
    }

    @DeleteMapping("/leave")
    @Operation(
            summary = "가족 탈퇴",
            description = """
                현재 사용자가 가족에서 탈퇴합니다.
                가족 소유자는 탈퇴할 수 없습니다.
                """
    )
    @SwaggerExceptionResponse({FAMILY_NOT_FOUND, MEMBER_NOT_IN_FAMILY, MEMBER_NOT_FOUND, INVALID_ACTION_FAMILY_BOSS})
    public ApiResponse<Void> leaveFamily(@AuthenticationPrincipal CustomOAuth2User currentUser) {
        familyService.leaveFamily(currentUser.getMember());
        return ApiResponse.noContent();
    }

}
