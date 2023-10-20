package com.example.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.auth.config.jwt.JwtTokenUtil;
import com.example.auth.config.jwt.JwtUserDetail;
import com.example.auth.dto.request.ReqLogin;
import com.example.auth.dto.request.ReqUser;
import com.example.auth.dto.response.ResUser;
import com.example.auth.entity.User;
import com.example.auth.repository.UserRepo;
import com.example.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.criteria.Predicate;
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
    @Autowired
    UserService userService;

    @BeforeEach
    void setUp() {
        Collection<GrantedAuthority> authorities = Collections.emptySet();
        JwtUserDetail userDetail = new JwtUserDetail(1L, "lala", "password", "fir", "la", authorities);
        java.util.Map<String, Object> claims = tokenUtil.generateUserToken(userDetail);
        log.info(claims.get("token").toString());
        token = "Bearer " + claims.get("token").toString();
    }

    @Test
    @DisplayName("cari user")
    public void getPaginationUser() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/user/search")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .queryParam("name", "la");

        mockMvc.perform(request)
                .andExpectAll(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].lastName", Matchers.contains("la")));

    }

    @Test
    void testLockingUser() {
        User dbUserBefore = userRepo.findById(1L).orElseThrow();
        ReqUser req = ReqUser.builder().id(1L).firstName("fi").lastName("ma").build();
        userService.updateUser(req);
        ReqUser req2 = ReqUser.builder().id(1L).firstName("mama").lastName("mia").build();
        userService.updateUser(req2);

        Specification<User> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("firstName"), "mama"));
            predicates.add(builder.equal(root.get("lastName"), "mia"));

            // disini digabungkan menggunakan and atau or
            Predicate andPredicate = (Predicate) builder.and(predicates.toArray(new Predicate[predicates.size()]));
            return query.where(andPredicate).getRestriction();
        };
        User dbUserAfter = userRepo.findOne(specification).orElseThrow();
         
        Assertions.assertEquals(dbUserAfter.getFirstName(), req2.getFirstName());
        Assertions.assertEquals(dbUserAfter.getLastName(), req2.getLastName());
        
        // harusnya versinya naik 2 
        log.info("user version before {}", dbUserBefore.getVersion().toString());
        log.info("user version after {}", dbUserAfter.getVersion().toString());
        Assertions.assertTrue(dbUserAfter.getVersion() > dbUserBefore.getVersion()); 
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

    @Test
    void testPrivateMethodTest() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Method privateMethod = UserService.class.getDeclaredMethod("privateMethod", String.class, Integer.class);
        privateMethod.setAccessible(true);
        // Invoke the private method on the instance
        String result = (String) privateMethod.invoke(userService, "john", 9);
        log.info(result);
        Assertions.assertEquals(result, "john9");
    }
}
