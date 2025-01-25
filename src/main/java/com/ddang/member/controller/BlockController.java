package com.ddang.member.controller;

import com.ddang.global.api.ApiResponse;
import com.ddang.global.exception.BadRequestException;
import com.ddang.global.exception.annotation.SwaggerExceptionResponse;
import com.ddang.member.oauth2.CustomOAuth2User;
import com.ddang.member.service.BlockService;
import com.ddang.member.service.response.BlockListResponse;
import com.ddang.member.service.response.BlockResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.ddang.global.exception.ErrorCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/block")
@Tag(name = "Block API", description = "차단 관련 API")
@Slf4j
public class BlockController {

    private final BlockService blockService;

    @PostMapping("/{blockedId}")
    @Operation(summary = "회원 차단", description = "회원을 차단합니다.")
    @SwaggerExceptionResponse({MEMBER_NOT_FOUND, ALREADY_BLOCKED_MEMBER, BLOCKED_MEMBER_IS_FAMILY_MEMBER})
    public ApiResponse<BlockResponse> createBlock(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                                  @PathVariable Long blockedId) {
        Long blockerId = customOAuth2User.getMember().getMemberId();
        return ApiResponse.created(blockService.createBlock(blockerId, blockedId));
    }

    @GetMapping("/list")
    @Operation(summary = "차단 목록 조회",
            description = "로그인한 사용자의 차단 목록을 최신순으로 10개씩 페이징하여 반환합니다.",
            parameters = {
                    @Parameter(name = "page", description = "조회할 페이지 번호 (기본값: 0)", required = false)
            })
    @SwaggerExceptionResponse({INVALID_PAGE_NUMBER, MEMBER_NOT_FOUND})
    public ApiResponse<Slice<BlockListResponse>> getBlockList(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                                              @RequestParam(defaultValue = "0") int page) {
        if (page < 0) {
            throw new BadRequestException(INVALID_PAGE_NUMBER);
        }
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Slice<BlockListResponse> blockList = blockService.getBlockList(customOAuth2User.getMember().getMemberId(), pageRequest);
        return ApiResponse.ok(blockList);
    }

    @DeleteMapping("/{blockId}")
    @Operation(summary = "차단 해제", description = "차단을 해제합니다.")
    @SwaggerExceptionResponse({BLOCK_NOT_FOUND})
    public ApiResponse<String> deleteBlock(@PathVariable Long blockId) {
        blockService.deleteBlock(blockId);
        return ApiResponse.ok("차단을 해제하였습니다.");
    }
}
