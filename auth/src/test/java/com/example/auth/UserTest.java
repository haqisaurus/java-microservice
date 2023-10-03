package com.example.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.auth.dto.response.ResUser;
import com.example.auth.service.UserService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @Test
	@DisplayName("Detail login test")
	public void getDetailUser() throws Exception {
        ResUser res = ResUser.builder().firstName("test").build();
        Mockito.when(userService.getUserDetail()).thenReturn(res);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/user/detail")
                                .characterEncoding("utf-8")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON);

                MvcResult mvcResult = mockMvc.perform(request)
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andDo(MockMvcResultHandlers.print())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("test"))
                                .andReturn();

                String actualResponseBody = mvcResult.getResponse().getContentAsString();
                log.info(actualResponseBody);
    }
}
