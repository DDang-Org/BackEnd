package com.ddang.walk.controller;

import com.ddang.global.api.ApiResponse;
import com.ddang.global.exception.ErrorCode;
import com.ddang.global.exception.annotation.SwaggerExceptionResponse;
import com.ddang.member.oauth2.CustomOAuth2User;
import com.ddang.walk.service.WalkLogService;
import com.ddang.walk.service.response.log.WalkLogByFamilyResponse;
import com.ddang.walk.service.response.log.WalkLogResponse;
import com.ddang.walk.service.response.log.WalkStaticsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/log")
@Tag(name = "DDang DDang log API", description = "댕댕로그 API")
public class WalkLogController {

    private final WalkLogService walkLogService;

    @Operation(summary = "산책한 날짜 리스트 조회", description = " 강아지가 각각 산책을 완료한 날짜의 리스트를 반환합니다. ")
    @SwaggerExceptionResponse(ErrorCode.NOT_MEMBER_DOG)
    @GetMapping("/{dogId}")
    public ApiResponse<List<LocalDate>> getWalkLogs(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                                    @PathVariable Long dogId){
        List<LocalDate> response = walkLogService.getWalkLogs(customOAuth2User.getMember(), dogId);
        return ApiResponse.ok(response);
    }

    @Operation(summary = "산책 내역 상세 조회", description = " 산책을 한 날짜의 상세 산책 내역을 조회합니다.")
    @SwaggerExceptionResponse({ErrorCode.NOT_MEMBER_DOG, ErrorCode.DOG_NOT_FOUND})
    @GetMapping("/date/{dogId}")
    public ApiResponse<List<WalkLogResponse>> getWalkLogByDate(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                                               @RequestParam(value = "selectDate") LocalDate selectDate,
                                                               @PathVariable Long dogId){
        List<WalkLogResponse> response = walkLogService.getWalkLogByDate(customOAuth2User.getMember(), selectDate, dogId);
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "올해 월별 산책 기록 조회",
            description = """
                    올해 월별로 나누어 산책 횟수를 조회합니다.
                    총 12개의 사이즈를 가진 리스트가 반환되며 0번인 달은
                    0으로 값이 들어가 있습니다.
                    """
    )
    @SwaggerExceptionResponse(ErrorCode.NOT_MEMBER_DOG)
    @GetMapping("/year")
    public ApiResponse<List<Integer>> getYearlyWalkLog(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        List<Integer> response = walkLogService.getYearlyWalkLog(customOAuth2User.getMember());
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "올해 가족 별 산책 기록",
            description = """
                    올해 가족 별로 나누어 산책 기록을 조회합니다. 로그인한 멤버는 가장 앞
                    리스트에 위치하고 나머지는 횟수별로 높은 순으로 순차적으로 나옵니다.
                    """
    )
    @SwaggerExceptionResponse(ErrorCode.NOT_MEMBER_DOG)
    @GetMapping("/year/family")
    public ApiResponse<List<WalkLogByFamilyResponse>> getYearlyWalkLogByFamily(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        List<WalkLogByFamilyResponse> response = walkLogService.getYearlyWalkLogByFamily(customOAuth2User.getMember());
        return ApiResponse.ok(response);
    }

    @Operation(summary = "총 산책 기록 조회", description = " 모든 강아지의 기준으로 전체 산책 기록의 통계를 조회합니다.")
    @SwaggerExceptionResponse(ErrorCode.NOT_MEMBER_DOG)
    @GetMapping("/total")
    public ApiResponse<WalkStaticsResponse> getTotalWalkLog(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        WalkStaticsResponse response = walkLogService.getTotalWalkLog(customOAuth2User.getMember());
        return ApiResponse.ok(response);
    }

    @Operation(summary = "이번달 산책 기록 조회", description = " 모든 강아지의 이번달 산책 기록의 통계를 조회합니다.")
    @SwaggerExceptionResponse(ErrorCode.NOT_MEMBER_DOG)
    @GetMapping("/total/month")
    public ApiResponse<WalkStaticsResponse> getMonthlyTotalWalk(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        WalkStaticsResponse response = walkLogService.getMonthlyTotalWalk(customOAuth2User.getMember());
        return ApiResponse.ok(response);
    }

}
