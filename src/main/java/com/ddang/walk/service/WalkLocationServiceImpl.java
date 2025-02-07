package com.ddang.walk.service;

import com.ddang.dog.entity.Dog;
import com.ddang.dog.repository.MemberDogRepository;
import com.ddang.global.api.WebSocketResponse;
import com.ddang.global.exception.BadRequestException;
import com.ddang.global.exception.MemberException;
import com.ddang.global.exception.NotFoundException;
import com.ddang.global.service.RedisService;
import com.ddang.member.entity.IsMatched;
import com.ddang.member.entity.Member;
import com.ddang.member.repository.MemberRepository;
import com.ddang.walk.service.request.DecisionWalkServiceRequest;
import com.ddang.walk.service.request.ProposalWalkServiceRequest;
import com.ddang.walk.service.request.StartWalkServiceRequest;
import com.ddang.walk.service.response.walk.*;
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
import java.util.stream.Collectors;

import static com.ddang.global.exception.ErrorCode.*;
import static com.ddang.global.service.RedisKey.*;
import static com.ddang.walk.util.DateCalculator.calculateAgeFromNow;


@Service
@RequiredArgsConstructor
@Slf4j
public class WalkLocationServiceImpl implements WalkLocationService {

    private final RedisService redisService;
    private final MemberDogRepository memberDogRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MemberRepository memberRepository;


    @Override
    @Transactional(readOnly = true)
    public void startWalk(String email, StartWalkServiceRequest startWalkServiceRequest){
        saveMemberLocation(email, startWalkServiceRequest);
        findNearbyMember(email);
    }

    @Override
    public void proposalWalk(String email, ProposalWalkServiceRequest proposalWalkServiceRequest) {
        Member member = getMemberFromEmailOrElseThrow(email);

        List<Dog> dogs = getDogsFromMemberId(member.getMemberId());
        String otherEmail = proposalWalkServiceRequest.otherMemberEmail();
        validateBeforeProposalWalk(email, otherEmail);

        redisService.setValues(PROPOSAL_KEY + member.getEmail(), otherEmail, Duration.ofMinutes(3));
        ProposalWalkResponse response = ProposalWalkResponse.of(dogs, member, proposalWalkServiceRequest.comment());

        sendMessageToWalkUrl(otherEmail, response);
        sendMessageToWalkUrl(member.getEmail(), "Proposal Success");
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

        Member otherMember = getMemberFromEmailOrElseThrow(memberEmail);
        List<Dog> dogs = getDogsFromMemberId(member.getMemberId());
        List<Dog> otherDogs = getDogsFromMemberId(otherMember.getMemberId());
        redisService.deleteValues(PROPOSAL_KEY + serviceRequest.otherEmail());

        if(serviceRequest.decision().equals("ACCEPT")){
            redisService.setValues(WALK_WITH_KEY + email, serviceRequest.otherEmail());
            redisService.setValues(WALK_WITH_KEY + serviceRequest.otherEmail(), email);
        }

        sendMessageToWalkUrl(email, DecisionWalkResponse.of(serviceRequest.decision(), otherMember, otherDogs));
        sendMessageToWalkUrl(serviceRequest.otherEmail(), DecisionWalkResponse.of(serviceRequest.decision(), member, dogs));
    }

    @Override
    public void startWalkWith(String email, StartWalkServiceRequest startWalkServiceRequest) {
        saveMemberLocation(email, startWalkServiceRequest);
        String otherMemberEmail = redisService.getValues(WALK_WITH_KEY + email);

        if(otherMemberEmail == null){
            throw new MemberException(INVALID_EMAIL);
        }

        sendMessageToWalkUrl(otherMemberEmail, WalkWithResponse.of(email, startWalkServiceRequest));
    }

    private void saveMemberLocation(String email, StartWalkServiceRequest startWalkServiceRequest){
        // 좌표 데이터를 String 변환
        String pointData = startWalkServiceRequest.toStringFormat();
        Point point = new Point(startWalkServiceRequest.longitude(), startWalkServiceRequest.latitude());

        redisService.setGeoValues(POINT_KEY, email, point);
        redisService.setListValues(LIST_KEY + email, pointData);
    }

    private void findNearbyMember(String email){
        Point memberLocation = redisService.getMemberPoint(POINT_KEY, email);
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisService.getNearbyMemberResults(memberLocation, 200, 10);
        List<String> memberEmailList = getEmailListFromNearbyMemberResults(results, email);

        if(memberEmailList.isEmpty()){
            sendMessageToWalkUrl(email, null);
            return;
        }

        List<MemberNearbyInfo> memberNearbyInfos = memberDogRepository.findDogsAndMembersByMemberEmails(memberEmailList);
        sendNearbyMember(memberNearbyInfos, email);
    }

    private void sendNearbyMember(List<MemberNearbyInfo> memberNearbyInfos, String email){
        List<MemberNearbyResponse> responseList = memberNearbyInfos.stream()
                .filter(memberNearbyInfo -> memberNearbyInfo.isMatched().equals(IsMatched.TRUE))
                .map(MemberNearbyResponse::from).toList();

        sendMessageToWalkUrl(email, responseList);
    }

    private List<String> getEmailListFromNearbyMemberResults(GeoResults<RedisGeoCommands.GeoLocation<String>> results, String email){
        List<String> memberEmailList = new ArrayList<>();

        for(GeoResult<RedisGeoCommands.GeoLocation<String>> result : results) {
            RedisGeoCommands.GeoLocation<String> location = result.getContent();
            String memberNearbyEmail = location.getName();

            if(!memberNearbyEmail.equals(email) && !redisService.checkHasKey(WALK_WITH_KEY + memberNearbyEmail)){
                memberEmailList.add(memberNearbyEmail);
            }
        }

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

    private List<Dog> getDogsFromMemberId(Long memberId){
        List<Dog> dogs = memberDogRepository.findDogsByMemberId(memberId);

        if(dogs.isEmpty()){
            throw new NotFoundException(DOG_NOT_FOUND);
        }

        return dogs;
    }

    private void validateBeforeProposalWalk(String email, String otherEmail){
        if(redisService.checkHasKey(PROPOSAL_KEY + email)){
            throw new BadRequestException(ALREADY_PROPOSAL);
        }

        if(redisService.checkHasKey(WALK_WITH_KEY + email) || redisService.checkHasKey(WALK_WITH_KEY + otherEmail)){
            throw new BadRequestException(ALREADY_MATCHED_MEMBER);
        }

    }

    public List<MemberNearbyResponse> groupByMember(List<MemberNearbyInfo> infos) {
        return infos.stream()
                .collect(Collectors.groupingBy(
                        MemberNearbyInfo::memberId, // memberId 기준으로 그룹화
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    List<MemberNearbyInfo> groupedInfos = entry.getValue();
                    List<DogInfo> dogInfos = groupedInfos.stream()
                            .map(info -> new DogInfo(
                                    info.dogId(), info.dogName(), info.breed(),
                                    info.dogProfileImg(), info.dogGender(),
                                    calculateAgeFromNow(info.dogBirthDate()), info.walkCount()
                            ))
                            .toList();

                    // 첫 번째 요소에서 email 가져오기
                    String email = groupedInfos.get(0).email();

                    return new MemberNearbyResponse(
                            dogInfos,
                            entry.getKey(), // memberId
                            email,
                            Type.WALK_ALONE // 적절한 타입 설정
                    );
                })
                .toList();
    }

}
