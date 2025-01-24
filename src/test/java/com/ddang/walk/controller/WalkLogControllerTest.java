package com.ddang.walk.controller;

import com.ddang.ApiTestSupport;
import com.ddang.global.entity.Gender;
import com.ddang.member.entity.*;
import com.ddang.member.oauth2.CustomOAuth2User;
import com.ddang.walk.entity.Walk;
import com.ddang.walk.service.response.log.WalkLogByFamilyResponse;
import com.ddang.walk.service.response.log.WalkLogResponse;
import com.ddang.walk.service.response.log.WalkStaticsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WalkLogControllerTest extends ApiTestSupport {

    @Test
    void getWalkLogs() throws Exception {
        //given
        Member member = setMemberToSecurity();

        List<LocalDate> response = new ArrayList<>();
        response.add(LocalDate.of(2025,1,1));

        String accessToken = jwtService.createAccessToken(member.getEmail(), "KAKAO");

        //when
        given(walkLogService.getWalkLogs(any(Member.class), eq(1L)))
                .willReturn(response);

        //then
        mockMvc.perform(get("/api/v1/log/{dogId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUC"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0]").value(response.get(0).toString()));
    }

    @Test
    void getWalkLogByDate() throws Exception {
        //given
        Member member = setMemberToSecurity();
        List<WalkLogResponse> responses = new ArrayList<>();

        Walk walk = Walk.builder()
                .walkImg("")
                .member(member)
                .totalCalorie(300)
                .totalDistance(3000)
                .startTime(LocalDateTime.of(2025,1,1,9,0,0))
                .endTime(LocalDateTime.of(2025,1,1,11,0,0))
                .build();

        responses.add(WalkLogResponse.from(walk));

        String accessToken = jwtService.createAccessToken(member.getEmail(), "KAKAO");

        //when
        given(walkLogService.getWalkLogByDate(any(Member.class), eq(LocalDate.of(2025,1,1)),eq(1L)))
                .willReturn(responses);

        //then
        mockMvc.perform(get("/api/v1/log/date/{dogId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("selectDate", LocalDate.of(2025,1,1).toString())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUC"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].walkImg").value(""))
                .andExpect(jsonPath("$.data[0].totalCalorie").value(300))
                .andExpect(jsonPath("$.data[0].totalDistanceMeter").value(3000))
                .andExpect(jsonPath("$.data[0].memberName").value("mjk"))
                .andExpect(jsonPath("$.data[0].memberProfileImg").value("profileImg1.png"));

    }

    @Test
    void getYearlyWalkLog() throws Exception {
        //given
        Member member = setMemberToSecurity();
        List<Integer> responses = List.of(1,0,0,0,
                0,0,0,0,
                0,0,0,0);

        String accessToken = jwtService.createAccessToken(member.getEmail(), "KAKAO");

        //when
        given(walkLogService.getYearlyWalkLog(any(Member.class),eq(1L)))
                .willReturn(responses);

        //then
        mockMvc.perform(get("/api/v1/log/year/{dogId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUC"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0]").value(1))
                .andExpect(jsonPath("$.data[1]").value(0));

    }

    @Test
    void getYearlyWalkLogByFamily() throws Exception {
        //given
        Member member = setMemberToSecurity();
        List<WalkLogByFamilyResponse> responses = new ArrayList<>();
        responses.add(WalkLogByFamilyResponse.of(member, 5));

        String accessToken = jwtService.createAccessToken(member.getEmail(), "KAKAO");

        //when
        given(walkLogService.getYearlyWalkLogByFamily(any(Member.class),eq(1L)))
                .willReturn(responses);

        //then
        mockMvc.perform(get("/api/v1/log/year/family/{dogId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUC"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].familyRole").value(FamilyRole.ELDER_BROTHER.name()))
                .andExpect(jsonPath("$.data[0].memberName").value("mjk"))
                .andExpect(jsonPath("$.data[0].count").value(5));
    }

    @Test
    void getTotalWalkLog() throws Exception {
        //given
        Member member = setMemberToSecurity();
        WalkStaticsResponse response = WalkStaticsResponse.of(3600, 5, 1000);

        String accessToken = jwtService.createAccessToken(member.getEmail(), "KAKAO");
        //when
        given(walkLogService.getTotalWalkLog(any(Member.class),eq(1L)))
                .willReturn(response);

        //then
        mockMvc.perform(get("/api/v1/log/total/{dogId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUC"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data.timeDuration.hours").value(1))
                .andExpect(jsonPath("$.data.walkCount").value(5))
                .andExpect(jsonPath("$.data.totalDistanceMeter").value(1000));
    }

    @Test
    void getMonthlyTotalWalk() throws Exception {
        //given
        Member member = setMemberToSecurity();
        WalkStaticsResponse response = WalkStaticsResponse.of(3600, 5, 1000);

        String accessToken = jwtService.createAccessToken(member.getEmail(), "KAKAO");
        //when
        given(walkLogService.getMonthlyTotalWalk(any(Member.class),eq(1L)))
                .willReturn(response);

        //then
        mockMvc.perform(get("/api/v1/log/total/month/{dogId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUC"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data.timeDuration.hours").value(1))
                .andExpect(jsonPath("$.data.walkCount").value(5))
                .andExpect(jsonPath("$.data.totalDistanceMeter").value(1000));
    }

    private Member setMemberToSecurity(){
        Member member = Member.builder()
                .name("mjk")
                .email("user@example.com")
                .role(Role.USER)
                .address("Incheon")
                .isMatched(IsMatched.TRUE)
                .gender(Gender.MALE)
                .provider(Provider.GOOGLE)
                .profileImg("profileImg1.png")
                .familyRole(FamilyRole.ELDER_BROTHER)
                .build();

        // Mock된 사용자 설정
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                Collections.emptySet(),
                Map.of("email", "user@example.com"),
                "email",
                member
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities())
        );

        return member;
    }
}