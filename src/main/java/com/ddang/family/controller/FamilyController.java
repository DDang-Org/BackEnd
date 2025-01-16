package com.ddang.family.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/family")
@RequiredArgsConstructor
@Tag(name = "Family API", description = "가족 생성, 조회 및 삭제 API")
public class FamilyController {
}
