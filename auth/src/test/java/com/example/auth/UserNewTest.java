package com.example.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.auth.config.jwt.JwtTokenUtil;
import com.example.auth.config.jwt.JwtUserDetail;
import com.example.auth.dto.request.ReqLogin;
import com.example.auth.dto.response.ResUser;
import com.example.auth.entity.User;
import com.example.auth.repository.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
public class UserNewTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    JwtTokenUtil tokenUtil;
    private String token;

    @BeforeEach
    void setUp() {
        Collection<GrantedAuthority> authorities = Collections.emptySet();
        JwtUserDetail userDetail = new JwtUserDetail(1L, "lala", "password", "fir", "la", authorities);
        java.util.Map<String, Object> claims = tokenUtil.generateUserToken(userDetail);
        log.info(claims.get("token").toString());
        token = "Bearer " + claims.get("token").toString();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        User dbUser = userRepo.findById(1L).orElseThrow();
        ReqLogin payload = ReqLogin.builder().username("test").password("test").build();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/user/detail")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", token);

        mockMvc.perform(request)
                .andExpectAll(MockMvcResultMatchers.status().isOk())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    ResUser resUser = objectMapper.readValue(response, ResUser.class);
                    log.info(response);
                    Assertions.assertNotNull(response);
                    Assertions.assertEquals(resUser.getId(), dbUser.getId());
                    Assertions.assertEquals(resUser.getLastName(), dbUser.getLastName()); 
                });
    }
}
