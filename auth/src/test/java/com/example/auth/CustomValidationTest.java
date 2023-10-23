package com.example.auth;

import com.example.auth.config.jwt.JwtTokenUtil;
import com.example.auth.config.jwt.JwtUserDetail;
import com.example.auth.dto.request.ReqBirthDate;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
public class CustomValidationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    JwtTokenUtil tokenUtil;
    private String token;
    @Autowired
    ObjectMapper objectMapper;
    @BeforeEach
    void setUp() {
        Collection<GrantedAuthority> authorities = Collections.emptySet();
        JwtUserDetail userDetail = new JwtUserDetail(1L, "lala", "password", "fir", "la", authorities);
        java.util.Map<String, Object> claims = tokenUtil.generateUserToken(userDetail);
        log.info(claims.get("token").toString());
        token = "Bearer " + claims.get("token").toString();
    }
    @Test
    @DisplayName("Test custom validation error")
    public void testCustomValidationBirthDateError() throws Exception {

        ReqBirthDate payload = ReqBirthDate.builder().dateOfBirth(new Date()).build();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/user/custom-validation")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(payload)) ;

        mockMvc.perform(request)
                .andExpectAll(MockMvcResultMatchers.status().isBadRequest())

                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[*]", Matchers.everyItem( Matchers.containsString("than 18"))  ));
    }
}
