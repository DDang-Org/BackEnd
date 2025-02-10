package com.ddang.member.service;

import com.ddang.member.controller.request.IsMatchedRequest;
import com.ddang.member.entity.*;
import com.ddang.global.entity.Gender;
import com.ddang.member.repository.MemberRepository;
import com.ddang.member.repository.WalkWithMemberRepository;
import com.ddang.member.service.request.JoinServiceRequest;
import com.ddang.member.service.request.UpdateServiceRequest;
import com.ddang.member.service.response.*;
import com.ddang.member.jwt.service.JwtService;
import com.ddang.notification.service.NotificationSettingsService;
import com.ddang.walk.repository.WalkRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static com.ddang.global.entity.Gender.MALE;
import static com.ddang.member.entity.FamilyRole.FATHER;
import static com.ddang.member.entity.IsMatched.TRUE;
import static com.ddang.member.entity.Provider.KAKAO;
import static com.ddang.member.entity.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private NotificationSettingsService notificationSettingsService;

    @Mock
    private WalkRepository walkRepository;

    @Mock
    private WalkWithMemberRepository walkWithMemberRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private MemberServiceImpl memberService;

    private JoinServiceRequest joinServiceRequest;
    private Member member;

    private final String VALID_REFRESH_TOKEN = "valid_refresh_token";
    private final String NEW_ACCESS_TOKEN = "new_access_token";
    private final String NEW_REFRESH_TOKEN = "new_refresh_token";
    private final String VALID_ACCESS_TOKEN = "valid_access_token";
    private final String EMAIL = "test@naver.com";

    @BeforeEach
    void setUp() {
        joinServiceRequest = new JoinServiceRequest(
                "test@naver.com",
                KAKAO,
                "홍길동",
                MALE,
                LocalDate.of(1990, 1, 1),
                "서울시 강남구",
                FATHER,
                1,
                TRUE,
                USER
        );

        member = Member.builder()
                .memberId(1L)
                .email(joinServiceRequest.email())
                .provider(joinServiceRequest.provider())
                .name(joinServiceRequest.memberName())
                .gender(joinServiceRequest.memberGender())
                .birthDate(joinServiceRequest.memberBirthDate())
                .address(joinServiceRequest.address())
                .familyRole(joinServiceRequest.familyRole())
                .profileImg(joinServiceRequest.memberProfileImg())
                .isMatched(joinServiceRequest.isMatched())
                .role(joinServiceRequest.role())
                .build();
    }

    @Test
    @DisplayName("회원가입 테스트")
    void joinTest() {
        // given
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
            Member savedMember = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedMember, "memberId", 1L);
            return savedMember;
        });

        when(jwtService.createAccessToken(anyString(), anyString())).thenReturn("mockAccessToken");
        when(jwtService.createRefreshToken(anyString())).thenReturn("mockRefreshToken");

        // when
        MemberResponse memberResponse = memberService.join(joinServiceRequest, response);

        // then
        assertThat(memberResponse).isNotNull();
        assertThat(memberResponse.memberId()).isEqualTo(1L);
        assertThat(memberResponse.email()).isEqualTo("test@naver.com");
        assertThat(memberResponse.provider()).isEqualTo(KAKAO);
        assertThat(memberResponse.memberName()).isEqualTo("홍길동");
        assertThat(memberResponse.memberGender()).isEqualTo(MALE);
        assertThat(memberResponse.memberBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(memberResponse.address()).isEqualTo("서울시 강남구");
        assertThat(memberResponse.familyRole()).isEqualTo(FATHER);
        assertThat(memberResponse.memberProfileImg()).isEqualTo(1);

        // verify
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(notificationSettingsService, times(1)).saveDefaultNotificationSettings(any(Member.class));
        verify(jwtService, times(1)).createAccessToken(anyString(), anyString());
        verify(jwtService, times(1)).createRefreshToken(anyString());
        verify(jwtService, times(1)).sendAccessAndRefreshToken(any(HttpServletResponse.class), anyString(), anyString());
        verify(jwtService, times(1)).saveRefreshTokenToRedis(anyString(), anyString());
    }

    @Test
    @DisplayName("AccessToken 재발급 테스트")
    void reissueAccessTokenTest() {
        // given
        when(jwtService.extractRefreshTokenFromCookie(request)).thenReturn(Optional.of(VALID_REFRESH_TOKEN));
        when(jwtService.isTokenValid(VALID_REFRESH_TOKEN)).thenReturn(true);
        when(jwtService.extractEmailFromRefreshToken(VALID_REFRESH_TOKEN)).thenReturn(Optional.of(EMAIL));
        when(jwtService.getRefreshTokenFromRedis(EMAIL)).thenReturn(Optional.of(VALID_REFRESH_TOKEN));
        when(memberRepository.findByEmail(EMAIL)).thenReturn(Optional.of(member));
        when(jwtService.createAccessToken(any(), any())).thenReturn(NEW_ACCESS_TOKEN);
        when(jwtService.createRefreshToken(any())).thenReturn(NEW_REFRESH_TOKEN);

        // when
        String resultToken = memberService.reissueAccessToken(request, response);

        // then
        assertThat(resultToken).isEqualTo(NEW_ACCESS_TOKEN);

        // verify
        verify(jwtService, times(1)).removeRefreshTokenFromRedis(EMAIL);
        verify(jwtService, times(1)).saveRefreshTokenToRedis(EMAIL, NEW_REFRESH_TOKEN);
        verify(jwtService, times(1)).sendAccessAndRefreshToken(response, NEW_ACCESS_TOKEN, NEW_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logoutTest() {
        // given
        when(jwtService.extractAccessToken(request)).thenReturn(Optional.of(VALID_ACCESS_TOKEN));
        when(jwtService.isTokenValid(VALID_ACCESS_TOKEN)).thenReturn(true);
        when(jwtService.extractEmail(VALID_ACCESS_TOKEN)).thenReturn(Optional.of(EMAIL));
        when(jwtService.getRefreshTokenFromRedis(EMAIL)).thenReturn(Optional.of("refresh_token"));

        // when
        String result = memberService.logout(request);

        // then
        assertThat(result).isEqualTo("Success Logout");

        // verify
        verify(jwtService, times(1)).removeRefreshTokenFromRedis(EMAIL);
    }

    @Test
    @DisplayName("회원 정보 조회 테스트")
    void getMemberInfoTest() {
        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // when
        MyPageResponse response = memberService.getMemberInfo(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.memberName()).isEqualTo("홍길동");
        assertThat(response.email()).isEqualTo("test@naver.com");

        // verify
        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("내 산책 정보 조회 테스트")
    void getMemberWalkInfoTest() {
        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(walkRepository.findTotalDistanceByMemberId(1L)).thenReturn(5000); // 5km
        when(walkRepository.countWalksByMemberId(1L)).thenReturn(10);
        when(walkWithMemberRepository.countBySenderMemberId(1L)).thenReturn(3);

        // when
        WalkInfoResponse response = memberService.getMemberWalkInfo(1L);

        // then
        assertThat(response.totalDistance()).isEqualTo(5.0);
        assertThat(response.walkCount()).isEqualTo(10);
        assertThat(response.countWalksWithMember()).isEqualTo(3);

        // verify
        verify(memberRepository, times(1)).findById(1L);
        verify(walkRepository, times(1)).findTotalDistanceByMemberId(1L);
        verify(walkRepository, times(1)).countWalksByMemberId(1L);
        verify(walkWithMemberRepository, times(1)).countBySenderMemberId(1L);
    }

    @Test
    @DisplayName("강번따 허용 여부 수정 테스트")
    void updateIsMatchedTest() {
        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // when
        IsMatchedResponse response = memberService.updateIsMatched(1L, new IsMatchedRequest("FALSE"));

        // then
        assertThat(response.isMatched()).isEqualTo(IsMatched.FALSE);

        // verify
        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("내 정보 수정을 위한 정보 조회 테스트")
    void getUpdateInfoTest() {
        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // when
        UpdateResponse response = memberService.getUpdateInfo(1L);

        // then
        assertThat(response.memberName()).isEqualTo("홍길동");
        assertThat(response.address()).isEqualTo("서울시 강남구");

        // verify
        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("내 정보 수정 테스트")
    void updateMemberTest() {
        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        UpdateServiceRequest updateRequest = new UpdateServiceRequest(
                "김철수", Gender.MALE, "부산시 해운대구", FamilyRole.ELDER_BROTHER, 2);

        // when
        UpdateResponse response = memberService.updateMember(1L, updateRequest);

        // then
        assertThat(response.memberName()).isEqualTo("김철수");
        assertThat(response.memberGender()).isEqualTo(Gender.MALE);
        assertThat(response.address()).isEqualTo("부산시 해운대구");
        assertThat(response.familyRole()).isEqualTo(FamilyRole.ELDER_BROTHER);
        assertThat(response.memberProfileImg()).isEqualTo(2);

        // verify
        verify(memberRepository, times(1)).findById(1L);
    }
}
