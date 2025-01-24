package com.ddang.member.controller.request;

import com.ddang.member.entity.FamilyRole;
import com.ddang.member.entity.IsMatched;
import com.ddang.member.entity.Provider;
import com.ddang.member.entity.Role;
import com.ddang.global.entity.Gender;
import com.ddang.member.service.request.JoinServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

@Schema(description = "회원가입 요청 데이터")
public record JoinRequest(

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "유효한 이메일 형식을 입력해주세요.")
        @Schema(description = "이메일", example = "test@naver.com")
        String email,

        @NotNull(message = "Provider를 입력해주세요.")
        @Schema(description = "Provider", example = "NAVER")
        Provider provider,

        @NotBlank(message = "이름을 입력해주세요.")
        @Schema(description = "이름", example = "홍길동")
        String memberName,

        @NotNull(message = "성별을 입력해주세요.")
        @Schema(description = "성별", example = "MALE")
        Gender memberGender,

        @PastOrPresent(message = "생년월일은 과거 혹은 현재 날짜여야 합니다.")
        @NotNull(message = "생일은 반드시 입력해야 합니다.")
        LocalDate memberBirthDate,

        @NotBlank(message = " 주소를 입력해주세요.")
        @Schema(description = "주소", example = "서울시 강남구")
        String address,

        @NotNull(message = "가족 역할을 입력해주세요.")
        @Schema(description = "가족 역할", example = "FATHER")
        FamilyRole familyRole,

        @NotNull(message = "프로필 이미지를 입력해주세요.")
        @Schema(description = "프로필 이미지", example = "https://example.com/profile.jpg")
        int memberProfileImg
) {

    public JoinServiceRequest toServiceRequest() {
        return new JoinServiceRequest(
                email,
                provider,
                memberName,
                memberGender,
                memberBirthDate,
                address,
                familyRole,
                memberProfileImg,
                IsMatched.TRUE,
                Role.USER
        );
    }
}
