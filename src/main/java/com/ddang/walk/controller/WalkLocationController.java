package com.ddang.walk.controller;

import com.ddang.global.aop.AuthenticationContext;
import com.ddang.global.aop.ExtractEmail;
import com.ddang.walk.controller.request.DecisionWalkRequest;
import com.ddang.walk.controller.request.ProposalWalkRequest;
import com.ddang.walk.controller.request.WalkRequest;
import com.ddang.walk.service.WalkLocationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Walk WebSocket API", description = "산책 웹소켓 API")
public class WalkLocationController {

    private final WalkLocationService walkLocationService;

    @MessageMapping("/api/v1/walk-alone")
    @ExtractEmail
    public void startWalk(SimpMessageHeaderAccessor headerAccessor ,@Payload @Valid WalkRequest walkRequest) {
        walkLocationService.startWalk(AuthenticationContext.getEmail() , walkRequest.toService());
    }

    @MessageMapping("/api/v1/proposal")
    @ExtractEmail
    public void proposalWalk(SimpMessageHeaderAccessor headerAccessor, @Payload @Valid ProposalWalkRequest proposalWalkRequest){
        walkLocationService.proposalWalk(AuthenticationContext.getEmail(), proposalWalkRequest.toService());
    }

    @MessageMapping("/api/v1/decision")
    @ExtractEmail
    public void decisionWalk(SimpMessageHeaderAccessor headerAccessor, @Payload @Valid DecisionWalkRequest decisionWalkRequest){
        walkLocationService.decisionWalk(AuthenticationContext.getEmail(), decisionWalkRequest.toService());
    }

    @MessageMapping("/api/v1/walk-with")
    @ExtractEmail
    public void startWalkWith(SimpMessageHeaderAccessor headerAccessor, @Payload @Valid WalkRequest walkRequest){
        walkLocationService.startWalkWith(AuthenticationContext.getEmail() , walkRequest.toService());
    }
}
