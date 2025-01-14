package com.ddang.member.controller;

import com.ddang.global.api.ApiResponse;
import com.ddang.member.controller.request.JoinRequest;
import com.ddang.member.entity.Member;
import com.ddang.member.oauth2.CustomOAuth2User;
import com.ddang.member.service.MemberService;
import com.ddang.member.service.response.MemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Tag(name = "Member API", description = "멤버 관련 API")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    @Operation(
            summary = "회원가입",
            description = "OAuth2 로그인 후 /register로 리디렉션 후 추가 정보 기입 후 회원가입을 완료합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원가입 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JoinRequest.class)
                    )
            )
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "회원가입 성공",
                    useReturnTypeSchema = true
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터가 유효하지 않은 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"성별을 입력해주세요\", \"data\": null}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부에서 처리되지 않은 오류가 발생한 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "서버 오류 예시",
                                    value = "{ \"code\": 500, \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"알 수 없는 오류가 발생했습니다.\", \"data\": null }"
                            )
                    )
            )
    })
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
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "AccessToken 재발급 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": 200, \"status\": \"OK\", \"message\": \"OK\", \"data\": \"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QG5...\"}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터가 유효하지 않은 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "존재하지 않는 회원",
                                            value = "{\"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"유저를 찾을 수 없습니다.\", \"data\": null}"
                                    ),
                                    @ExampleObject(
                                            name = "RefreshToken이 유효하지 않은 경우",
                                            value = "{\"code\": 400, \"status\": \"BAD_REQUEST\", \"message\": \"Redis에서 RefreshToken이 유효하지 않습니다.\", \"data\": null}"
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부에서 처리되지 않은 오류가 발생한 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "서버 오류 예시",
                                    value = "{ \"code\": 500, \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"알 수 없는 오류가 발생했습니다.\", \"data\": null }"
                            )
                    )
            )
    })
    public ApiResponse<String> reissue(HttpServletRequest request, HttpServletResponse response) {
        log.info("reissue() 메서드 진입");

        String newAccessToken = memberService.reissueAccessToken(request, response);
        log.info("새로운 AccessToken 생성: {}", newAccessToken);

        // ApiResponse 사용하여 응답 반환
        return ApiResponse.ok(newAccessToken);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "로그아웃을 수행합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": 200, \"status\": \"OK\", \"message\": \"OK\", \"data\": \"Success Logout\"}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 또는 유효하지 않은 토큰으로 접근하려는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패 예시",
                                    value = "{ \"code\": 401, \"status\": \"UNAUTHORIZED\", \"message\": \"AccessToken is invalid\", \"data\": null }"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부에서 처리되지 않은 오류가 발생한 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "서버 오류 예시",
                                    value = "{ \"code\": 500, \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"알 수 없는 오류가 발생했습니다.\", \"data\": null }"
                            )
                    )
            )
    })
    public ApiResponse<String> logout(HttpServletRequest request) {
        log.info("logout() 메서드 진입");
        return ApiResponse.ok(memberService.logout(request));
    }
}
