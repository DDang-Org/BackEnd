package com.ddang.member.controller;

import com.ddang.global.api.ApiResponse;
import com.ddang.global.exception.ErrorCode;
import com.ddang.global.exception.annotation.SwaggerExceptionResponse;
import com.ddang.member.controller.request.AddFriendRequest;
import com.ddang.member.oauth2.CustomOAuth2User;
import com.ddang.member.service.FriendService;
import com.ddang.member.service.response.FriendListResponse;
import com.ddang.member.service.response.FriendResponse;
import com.ddang.member.service.response.MemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friend")
@Tag(name = "Friend API", description = "친구 관련 API")
@Slf4j
public class FriendController {

    private final FriendService friendService;

    @PostMapping("")
    @Operation(
            summary = "친구 추가, 거절",
            description = """
                    친구 추가,거절 요청을 보냅니다. 상대도 이미 보냈으면 친구 추가를 진행합니다. 거절 시 친구 요청은 삭제됩니다.
                    decision 값은 ACCEPT 혹은 DENY 로 보내주세요
                    """
    )
    @SwaggerExceptionResponse({ErrorCode.MEMBER_NOT_FOUND, ErrorCode.MEMBER_ID_NULL, ErrorCode.DECISION_NULL})
    public ApiResponse<MemberResponse> addFriend(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                                 @RequestBody @Valid AddFriendRequest addFriendRequest){

        MemberResponse response = friendService.decideFriend(customOAuth2User.getMember(), addFriendRequest.toService());
        return ApiResponse.ok(response);
    }


    @Operation(
            summary = "친구 리스트 조회",
            description = "친구인 멤버들의 리스트를 조회 합니다."
    )
    @SwaggerExceptionResponse({})
    @GetMapping("")
    public ApiResponse<List<FriendListResponse> > getFriendList(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){

        List<FriendListResponse> response = friendService.getFriendList(customOAuth2User.getMember());
        return ApiResponse.ok(response);
    }

//    @Operation(
//            summary = "친구 상세 조회",
//            description = "친구의 프로필을 상세 조회합니다.",
//            parameters = {
//                    @Parameter(
//                            name = "memberId",
//                            description = "멤버 ID",
//                            required = true,
//                            in = ParameterIn.PATH,
//                            schema = @Schema(type = "long", example = "12345")
//                    )
//            }
//    )
//    @SwaggerExceptionResponse({ErrorCode.MEMBER_NOT_FOUND, ErrorCode.NOT_A_FRIEND, ErrorCode.DOG_NOT_FOUND})
//    @GetMapping("/{memberId}")
//    public ApiResponse<FriendResponse> getFriend(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
//                                                 @PathVariable(value = "memberId") Long memberId){
//
//        FriendResponse response = friendService.getFriend(customOAuth2User.getMember() ,memberId);
//        return ApiResponse.ok(response);
//    }

    @Operation(
            summary = "친구 삭제",
            description = "친구를 삭제합니다.",
            parameters = {
                    @Parameter(
                            name = "memberId",
                            description = "멤버 ID",
                            required = true,
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "long", example = "12345")
                    )
            }
    )
    @SwaggerExceptionResponse({ErrorCode.MEMBER_NOT_FOUND, ErrorCode.NOT_A_FRIEND})
    @DeleteMapping("/{memberId}")
    public ApiResponse<Void> deleteFriend(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                       @PathVariable(value = "memberId") Long memberId){

        friendService.deleteFriend(customOAuth2User.getMember() ,memberId);
        return ApiResponse.noContent();
    }
}
