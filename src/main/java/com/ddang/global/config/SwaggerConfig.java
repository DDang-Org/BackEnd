package com.ddang.global.config;

import com.ddang.global.exception.ErrorCode;
import com.ddang.global.exception.ErrorResponse;
import com.ddang.global.exception.SwaggerExampleHolder;
import com.ddang.global.exception.annotation.SwaggerExceptionResponse;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("DDang API 문서")
                .description("잘못된 부분이나 오류 발생 시 말씀해주세요.");

        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement addSecurityItem = new SecurityRequirement();
        addSecurityItem.addList("Bearer Token");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("Bearer Token", bearerAuth))
                .addSecurityItem(addSecurityItem)
                .info(info);
    }

    @Bean
    public OperationCustomizer applyCustomResponse() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            SwaggerExceptionResponse swaggerExceptionResponse =
                    handlerMethod.getMethodAnnotation(SwaggerExceptionResponse.class);
            generateErrorCodeResponseExample(operation, swaggerExceptionResponse);
            return operation;
        };
    }

    private void generateErrorCodeResponseExample(
            Operation operation,
            SwaggerExceptionResponse swaggerExceptionResponse
    ) {

        ApiResponses responses = operation.getResponses();

        List<ErrorCode> errorCodes = toExceptionStatusList(swaggerExceptionResponse);

        Map<Integer, List<SwaggerExampleHolder>> statusWithExampleHolders =
                generateStatusWithExampleHolders(errorCodes);

        addExamplesToResponses(responses, statusWithExampleHolders);
    }

    private List<ErrorCode> toExceptionStatusList(
            SwaggerExceptionResponse swaggerExceptionResponse) {

        List<ErrorCode> errorCodes = new ArrayList<>();
        errorCodes.addAll(Arrays.asList(swaggerExceptionResponse.value()));

        errorCodes.add(ErrorCode.INTERNAL_SERVER_ERROR);
        errorCodes.add(ErrorCode.UNAUTHORIZED_ATK_ERROR);
        errorCodes.add(ErrorCode.UNAUTHORIZED_RTK_ERROR);
        errorCodes.add(ErrorCode.EXPIRED_TOKEN_ERROR);
        errorCodes.add(ErrorCode.UNSUPPORTED_TOKEN_ERROR);
        errorCodes.add(ErrorCode.EMPTY_TOKEN_ERROR);


        return errorCodes;
    }

    private Map<Integer, List<SwaggerExampleHolder>> generateStatusWithExampleHolders(
            List<ErrorCode> errorCodes
    ) {
        return
                errorCodes.stream()
                        .map(
                                errorCode -> {
                                    try {
                                        return SwaggerExampleHolder.builder()
                                                .holder(getSwaggerExample(errorCode))
                                                .code(errorCode.getStatus().value())
                                                .name(errorCode.getCode())
                                                .build();
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                        .collect(groupingBy(SwaggerExampleHolder::getCode));
    }

    private Example getSwaggerExample(ErrorCode errorCode) {
        ErrorResponse errorResponse = ErrorResponse.from(errorCode);
        Example example = new Example();
        example.setValue(errorResponse);
        return example;
    }

    private void addExamplesToResponses(
            ApiResponses responses,
            Map<Integer, List<SwaggerExampleHolder>> statusWithExampleHolders) {
        statusWithExampleHolders.forEach(
                (status, v) -> {
                    Content content = new Content();
                    MediaType mediaType = new MediaType();
                    ApiResponse apiResponse = new ApiResponse();
                    v.forEach(
                            exampleHolder -> mediaType.addExamples(
                                    exampleHolder.getName(), exampleHolder.getHolder()));
                    content.addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                            mediaType);
                    apiResponse.setContent(content);
                    responses.addApiResponse(status.toString(), apiResponse);
                });
    }
}

