package com.example.auth;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;

import com.example.auth.config.jwt.JwtEntryPoint;
import com.example.auth.config.jwt.JwtTokenUtil;
import com.example.auth.config.jwt.JwtUserDetail;
import com.example.auth.dto.request.ReqLogin;
import com.example.auth.dto.response.ResLogin;
import com.example.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthApplicationTests {
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(AuthApplicationTests.class);

	@Autowired
	JwtTokenUtil tokenUtil;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {
	}

	@Test
	public void generateUserToken() {
		assertDoesNotThrow(() -> {
			Collection<GrantedAuthority> authorities = Collections.emptySet();
			JwtUserDetail userDetail = new JwtUserDetail(1L, "lala", "password", "fir", "la", authorities);
			Map<String, Object> token = tokenUtil.generateUserToken(userDetail);
			log.info(token.get("token").toString());

		});
	}

	@Test
	@DisplayName("Login test")
	public void loginTestShouldReturntoken() throws Exception {
		ResLogin response = new ResLogin();
		response.setToken("initoken");
		ReqLogin reqBody = ReqLogin.builder().username("lala").password("password").build();

		when(userService.performLogin(reqBody)).thenReturn(response);

		String payload = objectMapper.writeValueAsString(reqBody);
		log.info(payload);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/login")
				.characterEncoding("utf-8")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(payload);

		MvcResult mvcResult = mockMvc.perform(request)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.token").value(response.getToken()))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
		String actualResponseBody = mvcResult.getResponse().getContentAsString();
		log.info(actualResponseBody);

	}

}
