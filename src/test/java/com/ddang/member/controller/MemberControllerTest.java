package com.ddang.member.controller;

import com.ddang.member.controller.request.IsMatchedRequest;
import com.ddang.member.controller.request.JoinRequest;
import com.ddang.member.entity.*;
import com.ddang.member.oauth2.CustomOAuth2User;
import com.ddang.member.service.MemberServiceImpl;
import com.ddang.member.service.request.UpdateServiceRequest;
import com.ddang.member.service.response.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

import static com.ddang.global.entity.Gender.FEMALE;
import static com.ddang.global.entity.Gender.MALE;
import static com.ddang.member.entity.FamilyRole.FATHER;
import static com.ddang.member.entity.FamilyRole.MOTHER;
import static com.ddang.member.entity.IsMatched.TRUE;
import static com.ddang.member.entity.Provider.KAKAO;
import static com.ddang.member.entity.Role.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    MemberServiceImpl memberService;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("회원가입 테스트")
    @WithMockUser
    void joinTest() throws Exception {
        // given
        JoinRequest joinRequest = new JoinRequest(
                "test@naver.com",
                KAKAO,
                "홍길동",
                MALE,
                LocalDate.of(2021, 1, 1),
                "서울시 강남구",
                FATHER,
                1
        );
        MemberResponse dummyResponse = new MemberResponse(
                1L,
                "홍길동",
                "test@naver.com",
                KAKAO,
                MALE,
                LocalDate.of(2021, 1, 1),
                "서울시 강남구",
                FATHER,
                1
        );

        // when & then
        when(memberService.join(any(), any())).thenReturn(dummyResponse);
        mockMvc.perform(post("/api/v1/member/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("AccessToken 재발급 테스트")
    @WithMockUser
    void reissueTest() throws Exception {
        // given
        String dummyAccessToken = "dummyNewAccessToken";

        when(memberService.reissueAccessToken(any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(dummyAccessToken);

        // when & then
        mockMvc.perform(post("/api/v1/member/reissue")
                        .with(csrf()) // CSRF 보호를 우회하기 위한 토큰 추가
                        .cookie(new Cookie("refreshToken", "dummyRefreshToken"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(dummyAccessToken));
    }

    @Test
    @DisplayName("로그아웃 테스트")
    @WithMockUser
    void logoutTest() throws Exception {
        // given
        String result = "Success Logout";
        when(memberService.logout(any(HttpServletRequest.class))).thenReturn(result);

        // when & then
        mockMvc.perform(post("/api/v1/member/logout")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(result));
    }

    @Test
    @DisplayName("내 정보 조회 테스트")
    void getMyInfoTest() throws Exception {
        // given
        Member dummyMember = setMemberToSecurity();
        ReflectionTestUtils.setField(dummyMember, "memberId", 1L);

        MyPageResponse dummyResponse = MyPageResponse.from(dummyMember);

        when(memberService.getMemberInfo(1L)).thenReturn(dummyResponse);

        // when & then
        mockMvc.perform(get("/api/v1/member")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberId").value(1))
                .andExpect(jsonPath("$.data.memberName").value("홍길동"))
                .andExpect(jsonPath("$.data.email").value("test@naver.com"))
                .andExpect(jsonPath("$.data.address").value("서울시 강남구"))
                .andExpect(jsonPath("$.data.memberGender").value("MALE"))
                .andExpect(jsonPath("$.data.memberBirthDate").value("1990-01-01"))
                .andExpect(jsonPath("$.data.familyRole").value("FATHER"))
                .andExpect(jsonPath("$.data.memberProfileImg").value(1));
    }

    @Test
    @DisplayName("특정 멤버 조회 테스트")
    @WithMockUser
    void getMemberInfoWithIdTest() throws Exception {
        // given
        Member dummyMember = setMemberToSecurity();
        ReflectionTestUtils.setField(dummyMember, "memberId", 1L);

        MyPageResponse dummyResponse = MyPageResponse.from(dummyMember);

        when(memberService.getMemberInfo(1L)).thenReturn(dummyResponse);

        // when & then
        mockMvc.perform(get("/api/v1/member/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberId").value(1))
                .andExpect(jsonPath("$.data.memberName").value("홍길동"))
                .andExpect(jsonPath("$.data.email").value("test@naver.com"))
                .andExpect(jsonPath("$.data.address").value("서울시 강남구"))
                .andExpect(jsonPath("$.data.memberGender").value("MALE"))
                .andExpect(jsonPath("$.data.memberBirthDate").value("1990-01-01"))
                .andExpect(jsonPath("$.data.familyRole").value("FATHER"))
                .andExpect(jsonPath("$.data.memberProfileImg").value(1));
    }

    @Test
    @DisplayName("내 산책 정보 조회 테스트")
    @WithMockUser
    void getMyWalkInfoTest() throws Exception {
        // given
        Member dummyMember = setMemberToSecurity();
        ReflectionTestUtils.setField(dummyMember, "memberId", 1L);

        WalkInfoResponse dummyResponse = new WalkInfoResponse(
                12.5,
                5,
                3
        );

        when(memberService.getMemberWalkInfo(dummyMember.getMemberId())).thenReturn(dummyResponse);

        // when & then
        mockMvc.perform(get("/api/v1/member/walk-info")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalDistance").value(12.5))
                .andExpect(jsonPath("$.data.walkCount").value(5))
                .andExpect(jsonPath("$.data.countWalksWithMember").value(3));
    }

    @Test
    @DisplayName("강번따 허용 여부 수정 테스트")
    @WithMockUser
    void updateIsMatchedTest() throws Exception {
        // given
        Member dummyMember = setMemberToSecurity();
        ReflectionTestUtils.setField(dummyMember, "memberId", 1L);

        IsMatchedRequest isMatchedRequest = new IsMatchedRequest("TRUE");
        IsMatchedResponse dummyResponse = IsMatchedResponse.from(IsMatched.valueOf(isMatchedRequest.isMatched()));

        when(memberService.updateIsMatched(dummyMember.getMemberId(), isMatchedRequest)).thenReturn(dummyResponse);

        // when & then
        mockMvc.perform(patch("/api/v1/member/update/isMatched")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(isMatchedRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isMatched").value("TRUE"));
    }

    @Test
    @DisplayName("내 정보 수정을 위한 정보 조회 테스트")
    @WithMockUser
    void getUpdateInfoTest() throws Exception {
        // given
        Member dummyMember = setMemberToSecurity();
        ReflectionTestUtils.setField(dummyMember, "memberId", 1L);

        UpdateResponse dummyResponse = UpdateResponse.from(dummyMember);

        when(memberService.getUpdateInfo(dummyMember.getMemberId())).thenReturn(dummyResponse);

        // when & then
        mockMvc.perform(get("/api/v1/member/update")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberId").value(1))
                .andExpect(jsonPath("$.data.memberName").value("홍길동"))
                .andExpect(jsonPath("$.data.memberGender").value("MALE"))
                .andExpect(jsonPath("$.data.address").value("서울시 강남구"))
                .andExpect(jsonPath("$.data.familyRole").value("FATHER"))
                .andExpect(jsonPath("$.data.memberProfileImg").value(1));
    }

    @Test
    @DisplayName("내 정보 수정 테스트")
    @WithMockUser
    void updateMemberTest() throws Exception {
        // given
        Member dummyMember = setMemberToSecurity();
        ReflectionTestUtils.setField(dummyMember, "memberId", 1L);

        UpdateServiceRequest updateServiceRequest = new UpdateServiceRequest(
                "홍길순",
                FEMALE,
                "서울시 강북구",
                MOTHER,
                2
        );
        UpdateResponse dummyResponse = new UpdateResponse(
                1L,
                "홍길순",
                FEMALE,
                "서울시 강북구",
                MOTHER,
                2
        );

        when(memberService.updateMember(dummyMember.getMemberId(), updateServiceRequest)).thenReturn(dummyResponse);

        // when & then
        mockMvc.perform(patch("/api/v1/member/update")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateServiceRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberId").value(1))
                .andExpect(jsonPath("$.data.memberName").value("홍길순"))
                .andExpect(jsonPath("$.data.memberGender").value("FEMALE"))
                .andExpect(jsonPath("$.data.address").value("서울시 강북구"))
                .andExpect(jsonPath("$.data.familyRole").value("MOTHER"))
                .andExpect(jsonPath("$.data.memberProfileImg").value(2));
    }

    private Member setMemberToSecurity() {
        Member member = Member.builder()
                .name("홍길동")
                .email("test@naver.com")
                .role(USER)
                .address("서울시 강남구")
                .isMatched(TRUE)
                .gender(MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .provider(KAKAO)
                .profileImg(1)
                .familyRole(FATHER)
                .build();

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                Collections.emptySet(),
                Map.of("email", "test@naver.com"),
                "email",
                member
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities())
        );

        return member;
    }
}
