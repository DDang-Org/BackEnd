package com.ddang.walk.controller;

import com.ddang.global.api.ApiResponse;
import com.ddang.global.exception.BadRequestException;
import com.ddang.global.exception.ErrorCode;
import com.ddang.global.exception.annotation.SwaggerExceptionResponse;
import com.ddang.member.oauth2.CustomOAuth2User;
import com.ddang.walk.controller.request.CompleteWalkRequest;
import com.ddang.walk.controller.request.StartWalkRequest;
import com.ddang.walk.service.WalkService;
import com.ddang.walk.service.response.walk.CompleteWalkResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/walk")
@RequiredArgsConstructor
@Tag(name = "Walk API", description = "산책 API")
public class WalkController {

    private final WalkService walkService;

    @Operation(summary = "산책 시작", description = "산책할 강아지들을 선택 후 산책을 시작합니다.")
    @SwaggerExceptionResponse({ErrorCode.WALK_METER_NOT_NULL, ErrorCode.WALK_TIME_NOT_NULL, ErrorCode.ZERO_WALK_TIME, ErrorCode.ZERO_WALK_METER})
    @PostMapping("/start")
    public ApiResponse<Void> startWalk(@AuthenticationPrincipal CustomOAuth2User oAuth2User,
                                       @RequestBody @Valid StartWalkRequest startWalkRequest) throws IOException {

        walkService.startWalk(oAuth2User.getMember(), startWalkRequest.toService().dogIds());

        return ApiResponse.noContent();
    }

    @Operation(
            summary = "산책 완료",
            description = """
                    산책을 완료해 DB에 저장하고 관련한 소모 칼로리와 위도, 경도를 반환합니다.
                    요청 본문에 산책 시간 및 거리를 포함해야 합니다.
                    응답 값에 같이 산책한 유저가 없으면 walkWithDogInfo 값이 null 입니다.
                    """,
            parameters = {
            @Parameter( name = "walkImgFile",
                    description = "Walk Image File",
                    schema = @Schema(type = "string", format = "binary") )}
    )
    @SwaggerExceptionResponse({ErrorCode.WALK_METER_NOT_NULL, ErrorCode.WALK_TIME_NOT_NULL, ErrorCode.ZERO_WALK_TIME, ErrorCode.ZERO_WALK_METER})
    @PostMapping("/complete")
    public ApiResponse<CompleteWalkResponse> completeWalk(@AuthenticationPrincipal CustomOAuth2User oAuth2User,
                                                          @RequestPart @Valid CompleteWalkRequest request,
                                                          @RequestPart MultipartFile walkImgFile) throws IOException {

        return ApiResponse.ok(walkService.completeWalk(oAuth2User.getMember(), request.toServiceRequest(), walkImgFile));
    }

}
