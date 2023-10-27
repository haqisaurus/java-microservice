package com.example.auth;

import com.example.auth.config.jwt.JwtTokenUtil;
import com.example.auth.config.jwt.JwtUserDetail;
import com.example.auth.dto.request.ReqLogin;
import com.example.auth.dto.request.ReqUser;
import com.example.auth.dto.response.ResLogin;
import com.example.auth.entity.User;
import com.example.auth.repository.UserRepo;
import com.example.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

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

	@Autowired
	private UserService userService;

	@MockBean
	private UserRepo userRepo;

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
	@DisplayName("testing verify void function")
	public void userUpdateTest() {
		ReqUser reqData = ReqUser.builder().id(1L).firstName("la").lastName("mia").username("lamia").build();
		User updateData = new User();
		updateData.setId(1L);
		updateData.setFirstName("la");
		updateData.setLastName("mia");
		updateData.setUsername("lamia");
		Optional<User> userOption = Optional.of(updateData);
//		ketika method yang di panggil apa akan mengembalikan apa
		Mockito.when(userRepo.findById(reqData.getId())).thenReturn(userOption);
		userService.updateUser(reqData);
//		memastikan kalau method di repository terpanggil 1 x untun save dan find by id
		Mockito.verify(userRepo, Mockito.times(1)).save(updateData);
		Mockito.verify(userRepo, Mockito.times(1)).findById(reqData.getId());
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
