package com.ddang;

import com.ddang.dog.controller.DogController;
import com.ddang.dog.service.DogService;
import com.ddang.member.jwt.service.JwtService;
import com.ddang.walk.controller.WalkLogController;
import com.ddang.walk.service.WalkLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        DogController.class,
        WalkLogController.class
})
@AutoConfigureMockMvc(addFilters = false)
public abstract class ApiTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected DogService dogService;

    @MockitoBean
    protected JwtService jwtService;

    @MockitoBean
    protected JpaMetamodelMappingContext jpaMappingContext;

    @MockitoBean
    protected WalkLogService walkLogService;

}
