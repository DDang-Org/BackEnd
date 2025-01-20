package com.ddang.family.controller;

import com.ddang.family.service.FamilyService;
import com.ddang.family.service.response.InviteCodeResponse;
import com.ddang.global.api.ApiResponse;
import com.ddang.global.exception.annotation.SwaggerExceptionResponse;
import com.ddang.member.entity.Member;
import com.ddang.member.oauth2.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @SwaggerExceptionResponse({FAMILY_NOT_FOUND, MEMBER_NOT_IN_FAMILY ,MEMBER_NOT_FOUND})
    public ApiResponse<InviteCodeResponse> createInviteCode(@AuthenticationPrincipal CustomOAuth2User currentUser) {
        Member currentMember = currentUser.getMember();
        InviteCodeResponse response = familyService.createInviteCode(currentMember);
        return ApiResponse.created(response);
    }

}
