package com.ddang.dog.controller;

import com.ddang.ApiTestSupport;
import com.ddang.dog.controller.request.CreateDogRequest;
import com.ddang.dog.controller.request.UpdateDogRequest;
import com.ddang.dog.entity.Dog;
import com.ddang.dog.entity.IsNeutered;
import com.ddang.dog.service.response.DogResponse;
import com.ddang.dog.service.response.DogWalkResponse;
import com.ddang.family.entity.Family;
import com.ddang.global.entity.Gender;
import com.ddang.member.entity.*;
import com.ddang.member.oauth2.CustomOAuth2User;
import com.ddang.walk.service.response.TimeDuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DogControllerTest extends ApiTestSupport {

    @Test
    @DisplayName("강아지를 추가한다.")
    @WithMockUser
    void createDog() throws Exception {
        //given
        CreateDogRequest request = new CreateDogRequest("choco", "sigol",
                LocalDate.of(2023,1,1), BigDecimal.valueOf(3.5),
                Gender.MALE, IsNeutered.TRUE, "comment");

        Member member = setMemberToSecurity();

        DogResponse response = DogResponse.from(request.toServiceRequest().toEntity(null, Family.create()));
        String accessToken = jwtService.createAccessToken(member.getEmail(), "KAKAO");

        MockMultipartFile requestPart = new MockMultipartFile(
                "request", // @RequestPart 이름
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request) // JSON 직렬화
        );

        // 선택적 파일
        MockMultipartFile profileImgFile = new MockMultipartFile(
                "profileImgFile", // @RequestPart 이름
                "dog-profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "<<image-content>>".getBytes() // 실제 파일 데이터
        );

        //when
        given(dogService.createDog( eq(request.toServiceRequest()), any(Member.class), eq(profileImgFile)))
                .willReturn(response);

        //then
        mockMvc.perform(multipart("/api/v1/dogs/create")
                        .file(requestPart)
                        .file(profileImgFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("CRE"))
                .andExpect(jsonPath("$.status").value("Created"))
                .andExpect(jsonPath("$.message").value("CREATED"))
                .andExpect(jsonPath("$.data.dogName").value(request.dogName()))
                .andExpect(jsonPath("$.data.breed").value(request.breed()))
                .andExpect(jsonPath("$.data.dogBirthDate").value(request.dogBirthDate().toString()))
                .andExpect(jsonPath("$.data.weight").value(request.weight()))
                .andExpect(jsonPath("$.data.dogGender").value(request.dogGender().toString()))
                .andExpect(jsonPath("$.data.isNeutered").value(request.isNeutered().toString()))
                .andExpect(jsonPath("$.data.walkCount").value(0))
                .andExpect(jsonPath("$.data.comment").value(request.comment()));

    }

    @Test
    @DisplayName("강아지 ID 로 강아지를 조회한다.")
    @WithMockUser
    void getDogByDogId() throws Exception {
        //given
        Dog dog = Dog.builder()
                .birthDate(LocalDate.of(2023,1,1))
                .name("choco")
                .gender(Gender.MALE)
                .profileImg(null)
                .breed("sigol")
                .comment("comment")
                .isNeutered(IsNeutered.TRUE)
                .weight(BigDecimal.valueOf(3.5))
                .family(Family.create())
                .build();

        Member member = setMemberToSecurity();

        String accessToken = jwtService.createAccessToken(member.getEmail(), "KAKAO");
        DogResponse response = DogResponse.from(dog);

        //when
        given(dogService.getDogByDogId(1L))
                .willReturn(response);

        //then
        mockMvc.perform(get("/api/v1/dogs/{dogId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUC"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data.dogName").value(dog.getName()))
                .andExpect(jsonPath("$.data.breed").value(dog.getBreed()))
                .andExpect(jsonPath("$.data.dogBirthDate").value(dog.getBirthDate().toString()))
                .andExpect(jsonPath("$.data.weight").value(dog.getWeight()))
                .andExpect(jsonPath("$.data.dogGender").value(dog.getGender().toString()))
                .andExpect(jsonPath("$.data.isNeutered").value(dog.getIsNeutered().toString()))
                .andExpect(jsonPath("$.data.walkCount").value(0))
                .andExpect(jsonPath("$.data.comment").value(dog.getComment()));
    }

    @Test
    @WithMockUser
    @DisplayName("유저의 강아지를 조회한다.")
    void getMyDogs() throws Exception {
        //given
        Dog dog = Dog.builder()
                .birthDate(LocalDate.of(2023,1,1))
                .name("choco")
                .gender(Gender.MALE)
                .profileImg(null)
                .breed("sigol")
                .comment("comment")
                .isNeutered(IsNeutered.TRUE)
                .weight(BigDecimal.valueOf(3.5))
                .family(Family.create())
                .build();

        Member member = setMemberToSecurity();

        String accessToken = jwtService.createAccessToken(member.getEmail(), "KAKAO");
        DogResponse dogResponse = DogResponse.from(dog);
        List<DogResponse> response = new ArrayList<>();
        response.add(dogResponse);

        //when
        given(dogService.getDogsByMember(any(Member.class)))
                .willReturn(response);

        //then
        mockMvc.perform(get("/api/v1/dogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUC"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].dogName").value(dog.getName()))
                .andExpect(jsonPath("$.data[0].breed").value(dog.getBreed()))
                .andExpect(jsonPath("$.data[0].dogBirthDate").value(dog.getBirthDate().toString()))
                .andExpect(jsonPath("$.data[0].weight").value(dog.getWeight()))
                .andExpect(jsonPath("$.data[0].dogGender").value(dog.getGender().toString()))
                .andExpect(jsonPath("$.data[0].isNeutered").value(dog.getIsNeutered().toString()))
                .andExpect(jsonPath("$.data[0].walkCount").value(0))
                .andExpect(jsonPath("$.data[0].comment").value(dog.getComment()));
    }

    @Test
    @WithMockUser
    @DisplayName("강아지의 정보를 수정한다.")
    void updateDog() throws Exception {
        //given
        UpdateDogRequest updateDogRequest = new UpdateDogRequest("choco", "sigol",
                LocalDate.of(2023,1,1), BigDecimal.valueOf(3.5),
                Gender.MALE, IsNeutered.TRUE, "comment");

        Dog dog = Dog.builder()
                .birthDate(LocalDate.of(2023,1,1))
                .name("choco")
                .gender(Gender.MALE)
                .profileImg(null)
                .breed("sigol")
                .comment("comment")
                .isNeutered(IsNeutered.TRUE)
                .weight(BigDecimal.valueOf(3.5))
                .family(Family.create())
                .build();


        Member member = setMemberToSecurity();

        DogResponse response = DogResponse.from(dog);
        String accessToken = jwtService.createAccessToken(member.getEmail(), "KAKAO");

        MockMultipartFile requestPart = new MockMultipartFile(
                "request", // @RequestPart 이름
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(updateDogRequest) // JSON 직렬화
        );

        // 선택적 파일
        MockMultipartFile profileImgFile = new MockMultipartFile(
                "profileImgFile", // @RequestPart 이름
                "dog-profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "<<image-content>>".getBytes() // 실제 파일 데이터
        );

        //when
        given(dogService.updateDog( eq(updateDogRequest.toServiceRequest()), eq(1L), any(Member.class), eq(profileImgFile)))
                .willReturn(response);

        //then
        mockMvc.perform(multipart("/api/v1/dogs/{dogId}", 1L)
                        .file(requestPart)
                        .file(profileImgFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUC"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data.dogName").value(updateDogRequest.dogName()))
                .andExpect(jsonPath("$.data.breed").value(updateDogRequest.breed()))
                .andExpect(jsonPath("$.data.dogBirthDate").value(updateDogRequest.dogBirthDate().toString()))
                .andExpect(jsonPath("$.data.weight").value(updateDogRequest.weight()))
                .andExpect(jsonPath("$.data.dogGender").value(updateDogRequest.dogGender().toString()))
                .andExpect(jsonPath("$.data.isNeutered").value(updateDogRequest.isNeutered().toString()))
                .andExpect(jsonPath("$.data.walkCount").value(0))
                .andExpect(jsonPath("$.data.comment").value(updateDogRequest.comment()));
    }

    @Test
    @WithMockUser
    @DisplayName("강아지를 삭제한다.")
    void deleteDog() throws Exception {
        //given
        Member member = setMemberToSecurity();
        String accessToken = jwtService.createAccessToken(member.getEmail(), "KAKAO");

        //when
        doNothing().when(dogService).deleteDog(eq(1L),any(Member.class));

        //then
        mockMvc.perform(delete("/api/v1/dogs/{dogId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("NCT"))
                .andExpect(jsonPath("$.status").value("No Content"))
                .andExpect(jsonPath("$.message").value("No Content"));
    }

    @Test
    @WithMockUser
    @DisplayName("강아지의 당일 산책 기록을 조회한다.")
    void dogWalk() throws Exception {
        //given
        Member member = setMemberToSecurity();

        DogWalkResponse response = DogWalkResponse.of(TimeDuration.from(3600), 3000, 250);
        String accessToken = jwtService.createAccessToken(member.getEmail(), "KAKAO");

        //when
        given(dogService.dogWalk(any(Member.class), eq(1L)))
                .willReturn(response);

        //then
        mockMvc.perform(get("/api/v1/dogs/{dogId}/walk", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUC"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data.timeDuration.hours").value(1))
                .andExpect(jsonPath("$.data.timeDuration.minutes").value(0))
                .andExpect(jsonPath("$.data.timeDuration.seconds").value(0))
                .andExpect(jsonPath("$.data.totalDistanceMeter").value(3000))
                .andExpect(jsonPath("$.data.totalCalorie").value(250));
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
                .profileImg(1)
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