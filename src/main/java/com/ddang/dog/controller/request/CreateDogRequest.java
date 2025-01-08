package com.ddang.dog.controller.request;

import com.ddang.dog.entity.IsNeutered;
import com.ddang.dog.service.request.CreateDogServiceRequest;
import com.ddang.global.entity.Gender;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateDogRequest(
        @NotBlank(message = "이름은 비워둘 수 없습니다.")
        @Size(max = 10, message = "이름은 최대 10자까지 입력 가능합니다.")
        String dogName,

        @NotNull(message = "품종은 비워둘 수 없습니다.")
        String dogBreed,

        @PastOrPresent(message = "생년월일은 과거 혹은 현재 날짜여야 합니다.")
        LocalDate dogBirthDate,

        @DecimalMin(value = "1.00", message = "몸무게는 최소 1kg 이상이어야 합니다.")
        @DecimalMax(value = "100.00", message = "몸무게는 최대 100kg 이하여야 합니다.")
        @Digits(integer = 3, fraction = 2, message = "몸무게는 소수점 둘째 자리까지만 가능합니다.")
        BigDecimal dogWeight,

        @NotNull(message = "성별은 반드시 입력해야 합니다.")
        Gender dogGender,

        @NotNull(message = "중성화 여부는 반드시 입력해야 합니다.")
        IsNeutered isNeutered,

        @Size(max = 30, message = "코멘트는 최대 30자까지 입력 가능합니다.")
        String comment
) {
    public CreateDogServiceRequest toServiceRequest() {
        return new CreateDogServiceRequest(
                dogName,
                dogBreed,
                dogBirthDate,
                dogWeight,
                dogGender,
                isNeutered,
                comment
        );
    }
}

