package com.ddang.walk.service;

import com.ddang.dog.entity.Dog;
import com.ddang.dog.service.response.DogResponse;
import com.ddang.global.api.WebSocketResponse;
import com.ddang.global.exception.BadRequestException;
import com.ddang.global.exception.MemberException;
import com.ddang.global.exception.NotFoundException;
import com.ddang.global.service.RedisService;
import com.ddang.member.entity.Member;
import com.ddang.member.repository.MemberRepository;
import com.ddang.walk.service.request.DecisionWalkServiceRequest;
import com.ddang.walk.service.request.ProposalWalkServiceRequest;
import com.ddang.walk.service.request.WalkServiceRequest;
import com.ddang.walk.service.response.walk.DecisionWalkResponse;
import com.ddang.walk.service.response.walk.MemberNearbyResponse;
import com.ddang.walk.service.response.walk.ProposalWalkResponse;
import com.ddang.walk.service.response.walk.WalkWithResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.ddang.global.exception.ErrorCode.*;
import static com.ddang.global.service.RedisKey.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class WalkLocationServiceImpl implements WalkLocationService {

    private final RedisService redisService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MemberRepository memberRepository;


    @Override
    @Transactional(readOnly = true)
    public void startWalk(String email, WalkServiceRequest walkServiceRequest){
        saveMemberLocation(email, walkServiceRequest);
        findNearbyMember(email);
    }

    @Override
    public void proposalWalk(String email, ProposalWalkServiceRequest proposalWalkServiceRequest) {
        DogResponse dog = getDogFromRedis(email);
        String otherEmail = proposalWalkServiceRequest.otherMemberEmail();
        validateBeforeProposalWalk(email, otherEmail);

        redisService.setValues(PROPOSAL_KEY + email, otherEmail, Duration.ofMinutes(3));
        ProposalWalkResponse response = ProposalWalkResponse.of(dog, email, proposalWalkServiceRequest.comment());

        sendMessageToWalkUrl(otherEmail, response);
        sendMessageToWalkUrl(email, "Proposal Success");
    }

    @Override
    public void decisionWalk(String email, DecisionWalkServiceRequest serviceRequest) {
        Member member = getMemberFromEmailOrElseThrow(email);
        String memberEmail = redisService.getValues(PROPOSAL_KEY + serviceRequest.otherEmail());

        if(memberEmail == null){
            throw new BadRequestException(NOT_EXIST_PROPOSAL);
        }

        if(!memberEmail.equals(email)){
            throw new BadRequestException(NOT_MATCHED_MEMBER);
        }

        Member otherMember = getMemberFromEmailOrElseThrow(serviceRequest.otherEmail());
        DogResponse dog = getDogFromRedis(email);
        DogResponse otherDog = getDogFromRedis(serviceRequest.otherEmail());
        redisService.deleteValues(PROPOSAL_KEY + serviceRequest.otherEmail());

        if(serviceRequest.decision().equals("ACCEPT")){
            redisService.setValues(WALK_WITH_KEY + email, serviceRequest.otherEmail());
            redisService.setValues(WALK_WITH_KEY + serviceRequest.otherEmail(), email);
        }

        sendMessageToWalkUrl(email, DecisionWalkResponse.of(serviceRequest.decision(), otherMember, otherDog));
        sendMessageToWalkUrl(serviceRequest.otherEmail(), DecisionWalkResponse.of(serviceRequest.decision(), member, dog));
    }

    @Override
    public void startWalkWith(String email, WalkServiceRequest walkServiceRequest) {
        saveMemberLocation(email, walkServiceRequest);
        String otherMemberEmail = redisService.getValues(WALK_WITH_KEY + email);

        if(otherMemberEmail == null){
            throw new MemberException(INVALID_EMAIL);
        }

        sendMessageToWalkUrl(otherMemberEmail, WalkWithResponse.of(email, walkServiceRequest));
    }

    private void saveMemberLocation(String email, WalkServiceRequest request){
        // 좌표 데이터를 String 변환
        Point point = new Point(request.longitude(), request.latitude());

        redisService.setGeoValues(POINT_KEY, email, point);
    }

    private void findNearbyMember(String email){
        Point memberLocation = redisService.getMemberPoint(POINT_KEY, email);
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisService.getNearbyMemberResults(memberLocation, 200, 10);
        List<String> memberEmailList = getEmailListFromNearbyMemberResults(results, email);

        if(memberEmailList.isEmpty()){
            sendMessageToWalkUrl(email, null);
            return;
        }
        List<MemberNearbyResponse> nearbyResponses = getNearbyResponseFromRedis(memberEmailList);

        sendMessageToWalkUrl(email, nearbyResponses);
    }

    private List<MemberNearbyResponse> getNearbyResponseFromRedis(List<String> memberEmailList){
        return memberEmailList.stream()
                .map(email -> {
                    try {
                        DogResponse dog = (DogResponse) redisService.loadHash(WALK_DOG_KEY + email);
                        return MemberNearbyResponse.of(dog, email);
                    } catch (ClassCastException e) {
                        log.error("key 로 가져온 객체의 형식이 다릅니다. {}: {}", WALK_DOG_KEY + email, e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public DogResponse getDogFromRedis(String email) {
        Object obj = redisService.loadHash(WALK_DOG_KEY + email);

        if (!(obj instanceof DogResponse)) {
            throw new NotFoundException(DOG_NOT_FOUND);
        }

        return (DogResponse) obj;
    }

    private List<String> getEmailListFromNearbyMemberResults(GeoResults<RedisGeoCommands.GeoLocation<String>> results, String email){
        List<String> memberEmailList = new ArrayList<>();
        List<String> blockEmails = getBlockEmailsFromRedis(email);

        for(GeoResult<RedisGeoCommands.GeoLocation<String>> result : results) {
            RedisGeoCommands.GeoLocation<String> location = result.getContent();
            String memberNearbyEmail = location.getName();

            if(!memberNearbyEmail.equals(email) && !redisService.checkHasKey(WALK_WITH_KEY + memberNearbyEmail)){
                memberEmailList.add(memberNearbyEmail);
            }
        }

        memberEmailList.removeAll(blockEmails);
        return memberEmailList;
    }

    private void sendMessageToWalkUrl(String email, Object data){
        messagingTemplate.convertAndSend("/sub/walk/" + email,  WebSocketResponse.ok(data));
        log.info("Message sent to /sub/walk/" + email + " with data: " + data);
    }

    private Member getMemberFromEmailOrElseThrow(String email){
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MEMBER_NOT_FOUND));
    }

    private void validateBeforeProposalWalk(String email, String otherEmail){
        if(redisService.checkHasKey(PROPOSAL_KEY + email)){
            throw new BadRequestException(ALREADY_PROPOSAL);
        }

        if(redisService.checkHasKey(WALK_WITH_KEY + email) || redisService.checkHasKey(WALK_WITH_KEY + otherEmail)){
            throw new BadRequestException(ALREADY_MATCHED_MEMBER);
        }

    }

    private List<String> getBlockEmailsFromRedis(String email){
        List<String> blockEmails = redisService.getStringListOpsValues(BLOCK_LIST_KEY + email);

        return blockEmails;
    }


}
