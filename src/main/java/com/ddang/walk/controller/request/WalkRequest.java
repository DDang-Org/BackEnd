package com.ddang.walk.controller.request;

import com.ddang.walk.service.request.WalkServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Schema(description = "위치 요청 DTO")
public record WalkRequest(

        @Schema(description = "위도 (-90.0 ~ 90.0)", example = "37.5665")
        @NotNull(message = "위도는 필수입니다.")
        @DecimalMin(value = "-90.0", message = "위도는 -90.0 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90.0 이하여야 합니다.")
        Double latitude,

        @Schema(description = "경도 (-180.0 ~ 180.0)", example = "126.9780")
        @NotNull(message = "경도는 필수입니다.")
        @DecimalMin(value = "-180.0", message = "경도는 -180.0 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180.0 이하여야 합니다.")
        Double longitude
) {

    public WalkServiceRequest toService() {
        return new WalkServiceRequest(latitude, longitude);
    }
}

