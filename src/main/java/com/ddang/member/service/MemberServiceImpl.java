package com.ddang.member.service;

import com.ddang.dog.repository.MemberDogRepository;
import com.ddang.family.entity.Family;
import com.ddang.family.repository.FamilyRepository;
import com.ddang.global.exception.AuthenticationException;
import com.ddang.global.exception.ErrorCode;
import com.ddang.global.exception.MemberException;
import com.ddang.member.controller.request.IsMatchedRequest;
import com.ddang.member.entity.IsMatched;
import com.ddang.member.entity.Member;
import com.ddang.member.jwt.service.JwtService;
import com.ddang.member.repository.FriendRepository;
import com.ddang.member.repository.MemberRepository;
import com.ddang.member.repository.WalkWithMemberRepository;
import com.ddang.member.service.request.JoinServiceRequest;
import com.ddang.member.service.request.UpdateServiceRequest;
import com.ddang.member.service.response.*;
import com.ddang.notification.service.NotificationSettingsService;
import com.ddang.walk.repository.WalkRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final WalkRepository walkRepository;
    private final WalkWithMemberRepository walkWithMemberRepository;
    private final JwtService jwtService;
    private final NotificationSettingsService notificationSettingsService;
    private final MemberDogRepository memberDogRepository;
    private final FriendRepository friendRepository;
    private final FamilyRepository familyRepository;

    @Override
    public MemberResponse join(JoinServiceRequest serviceRequest, HttpServletResponse response) {

        Member member = serviceRequest.toEntity();
        throwIfExistEmail(member.getEmail());

        memberRepository.save(member);

        notificationSettingsService.saveDefaultNotificationSettings(member);

        String accessToken = jwtService.createAccessToken(member.getEmail(), member.getProvider().name());
        String refreshToken = jwtService.createRefreshToken(member.getEmail());

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.saveRefreshTokenToRedis(member.getEmail(), refreshToken);

        return MemberResponse.from(member);
    }

    @Override
    public String reissueAccessToken(String email, HttpServletResponse response) {

        jwtService.getRefreshTokenFromRedis(email)
                .filter(jwtService::isTokenValid)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.UNAUTHORIZED_RTK_ERROR));

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        String newAccessToken = jwtService.createAccessToken(member.getEmail(), member.getProvider().name());
        String newRefreshToken = jwtService.createRefreshToken(member.getEmail());

        jwtService.removeRefreshTokenFromRedis(email); // 기존 토큰 삭제
        jwtService.saveRefreshTokenToRedis(member.getEmail(), newRefreshToken); // 새로운 토큰 저장

        jwtService.sendAccessAndRefreshToken(response, newAccessToken, newRefreshToken);

        return newAccessToken;
    }

    @Override
    public String logout(HttpServletRequest request) {

        String accessToken = jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.UNAUTHORIZED_ATK_ERROR));

        String email = jwtService.extractEmail(accessToken)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.UNAUTHORIZED_ATK_ERROR));

        if (jwtService.getRefreshTokenFromRedis(email).isPresent()) {
            jwtService.removeRefreshTokenFromRedis(email);
        }

        return "Success Logout";
    }


    @Override
    @Transactional(readOnly = true)
    public MyPageResponse getMyInfo(Long memberId) {
        Member member = findMemberById(memberId);

        return MyPageResponse.from(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberPageResponse getMemberInfo(Long memberId) {
        Member member = findMemberById(memberId);

        return MemberPageResponse.from(member);
    }

    @Override
    @Transactional(readOnly = true)
    public WalkInfoResponse getMemberWalkInfo(Long memberId) {
        findMemberById(memberId);

        int totalDistanceInMeters = walkRepository.findTotalDistanceByMemberId(memberId);
        int countWalks = walkRepository.countWalksByMemberId(memberId);

        double totalDistanceInKilometers = totalDistanceInMeters / 1000.0;
        int countWalksWithMember = walkWithMemberRepository.countBySenderMemberId(memberId);

        return WalkInfoResponse.from(totalDistanceInKilometers, countWalks, countWalksWithMember);
    }

    @Override
    public IsMatchedResponse updateIsMatched(Long memberId, IsMatchedRequest isMatchedRequest) {
        Member member = findMemberById(memberId);
        IsMatched isMatched = validateIsMatched(isMatchedRequest.isMatched());

        member.updateIsMatched(isMatched);
        return IsMatchedResponse.from(member.getIsMatched());
    }

    @Override
    @Transactional(readOnly = true)
    public UpdateResponse getUpdateInfo(Long memberId) {
        Member member = findMemberById(memberId);

        return UpdateResponse.from(member);
    }

    @Override
    public UpdateResponse updateMember(Long memberId, UpdateServiceRequest serviceRequest) {
        Member member = findMemberById(memberId);

        serviceRequest.toEntity(member);

        return UpdateResponse.from(member);
    }

    @Override
    public void deleteMember(Member member) {
        Member currentMember = findMemberById(member.getMemberId());

        if (currentMember.getFamily() != null) {
            Family family = currentMember.getFamily();

            // 가족 대표인 경우
            if (family.getRepresentativeMemberId().equals(currentMember.getMemberId())) {
                int familyMemberCount = memberRepository.countByFamilyId(family.getFamilyId());
                // 가족에 다른 구성원이 있을 경우 삭제 불가
                if (familyMemberCount > 1) {
                    throw new MemberException(ErrorCode.CANNOT_DELETE_REPRESENTATIVE);
                } else {
                    // 대표이지만 본인만 있을 경우
                    memberDogRepository.softDeleteByMember(currentMember);
                    currentMember.updateFamily(null);
                    familyRepository.softDeleteById(family.getFamilyId());
                }
            } else {
                // 가족 구성원인 경우
                memberDogRepository.softDeleteByMember(currentMember);
                currentMember.updateFamily(null);
            }
        }

        // 가족 여부와 관계없이 항상 실행되는 공통 처리: 알림, 친구, 멤버, 토큰 삭제
        notificationSettingsService.deleteNotificationSettings(currentMember.getMemberId());
        friendRepository.deleteByMemberId(currentMember.getMemberId());
        memberRepository.softDeleteById(currentMember.getMemberId());

        if (jwtService.getRefreshTokenFromRedis(currentMember.getEmail()).isPresent()) {
            jwtService.removeRefreshTokenFromRedis(currentMember.getEmail());
        }
    }


    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private IsMatched validateIsMatched(String isMatchedValue) {
        try {
            return IsMatched.valueOf(isMatchedValue);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new MemberException(ErrorCode.INVALID_IS_MATCHED);
        }
    }

    private void throwIfExistEmail(String email){
        if(memberRepository.existsByEmail(email)){
            throw new MemberException(ErrorCode.EXIST_EMAIL);
        }
    }
}
