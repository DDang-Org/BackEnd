package com.ddang.member.controller;

import com.ddang.global.api.ApiResponse;
import com.ddang.global.exception.annotation.SwaggerExceptionResponse;
import com.ddang.member.controller.request.IsMatchedRequest;
import com.ddang.member.controller.request.JoinRequest;
import com.ddang.member.controller.request.UpdateRequest;
import com.ddang.member.oauth2.CustomOAuth2User;
import com.ddang.member.service.MemberService;
import com.ddang.member.service.response.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import static com.ddang.global.exception.ErrorCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Tag(name = "Member API", description = "멤버 관련 API")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    @Operation(summary = "회원가입", description = "OAuth2 로그인 후 /register로 리디렉션 후 추가 정보 기입 후 회원가입을 완료합니다.")
    @SwaggerExceptionResponse({INVALID_EMAIL, PROVIDER_NOT_NULL, MEMBER_NAME_NOT_NULL, MEMBER_GENDER_NOT_NULL,
            MEMBER_BIRTH_DATE_MUST_BE_PAST_OR_PRESENT, MEMBER_ADDRESS_NOT_NULL, MEMBER_FAMILY_ROLE_NOT_NULL, MEMBER_PROFILE_IMG_NOT_NULL})
    public ApiResponse<MemberResponse> join(@RequestBody @Valid JoinRequest joinRequest,
                                            HttpServletResponse response) {

        return ApiResponse.created(memberService.join(joinRequest.toServiceRequest(), response));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/reissue")
    @Operation(
            summary = "AccessToken 재발급",
            description = "RefreshToken을 사용하여 새로운 AccessToken을 발급합니다. 모든 요청 시 AccessToken의 유효기간이 지나 401을 반환받은 경우 /reissue로 재발급 받아 사용합니다."
    )
    @SwaggerExceptionResponse({UNAUTHORIZED_RTK_ERROR, MEMBER_NOT_FOUND})
    public ApiResponse<String> reissue(HttpServletRequest request, HttpServletResponse response) {
        log.info("reissue() 메서드 진입");

        String newAccessToken = memberService.reissueAccessToken(request, response);
        log.info("새로운 AccessToken 생성: {}", newAccessToken);

        // ApiResponse 사용하여 응답 반환
        return ApiResponse.ok(newAccessToken);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃을 수행합니다.")
    @SwaggerExceptionResponse({UNAUTHORIZED_ATK_ERROR})
    public ApiResponse<String> logout(HttpServletRequest request) {
        log.info("logout() 메서드 진입");
        return ApiResponse.ok(memberService.logout(request));
    }

    @GetMapping
    @Operation(summary = "내 정보 조회", description = "내 정보를 조회합니다.")
    @SwaggerExceptionResponse({MEMBER_NOT_FOUND})
    public ApiResponse<MyPageResponse> getMyInfo(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        return ApiResponse.ok(memberService.getMemberInfo(customOAuth2User.getMember().getMemberId()));
    }

    @GetMapping("/{memberId}")
    @Operation(summary = "특정 멤버 정보 조회", description = "특정 멤버의 정보를 조회합니다.")
    @SwaggerExceptionResponse({MEMBER_NOT_FOUND})
    public ApiResponse<MyPageResponse> getMemberInfoWithId(@PathVariable Long memberId) {
        return ApiResponse.ok(memberService.getMemberInfo(memberId));
    }

    @GetMapping("/walk-info")
    @Operation(summary = "내 산책 정보 조회", description = "내 산책 정보를 조회합니다.")
    @SwaggerExceptionResponse({MEMBER_NOT_FOUND})
    public ApiResponse<WalkInfoResponse> getMyWalkInfo(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        return ApiResponse.ok(memberService.getMemberWalkInfo(customOAuth2User.getMember().getMemberId()));
    }

    @PatchMapping("/update/isMatched")
    @Operation(summary = "강번따 허용 여부 수정", description = "강아지 번따 허용 여부를 수정합니다.")
    @SwaggerExceptionResponse({MEMBER_NOT_FOUND, INVALID_IS_MATCHED})
    public ApiResponse<IsMatchedResponse> updateIsMatched(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @RequestBody @Valid IsMatchedRequest isMatchedRequest) {

        Long memberId = customOAuth2User.getMember().getMemberId();
        return ApiResponse.ok(memberService.updateIsMatched(memberId, isMatchedRequest));
    }

    @GetMapping("/update")
    @Operation(summary = "내 정보 수정을 위한 정보 조회", description = "내 정보 수정을 위한 정보를 조회합니다.")
    @SwaggerExceptionResponse({MEMBER_NOT_FOUND})
    public ApiResponse<UpdateResponse> getUpdateInfo(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        return ApiResponse.ok(memberService.getUpdateInfo(customOAuth2User.getMember().getMemberId()));
    }

    @PatchMapping("/update")
    @Operation(summary = "내 정보 수정", description = "내 정보를 수정합니다.")
    @SwaggerExceptionResponse({MEMBER_NOT_FOUND, MEMBER_NAME_NOT_NULL, MEMBER_GENDER_NOT_NULL,
            MEMBER_ADDRESS_NOT_NULL, MEMBER_FAMILY_ROLE_NOT_NULL, MEMBER_PROFILE_IMG_NOT_NULL})
    public ApiResponse<UpdateResponse> updateMember(@RequestBody @Valid UpdateRequest updateRequest,
                                                    @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        return ApiResponse.ok(memberService.updateMember(customOAuth2User.getMember().getMemberId(), updateRequest.toServiceRequest()));
    }
}
