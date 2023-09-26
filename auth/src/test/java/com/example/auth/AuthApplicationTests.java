package com.example.auth;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import com.example.auth.config.jwt.JwtEntryPoint;
import com.example.auth.config.jwt.JwtTokenUtil;
import com.example.auth.config.jwt.JwtUserDetail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AuthApplicationTests { 
	    private static final Logger log = org.slf4j.LoggerFactory.getLogger(AuthApplicationTests.class);

	@Autowired
	JwtTokenUtil tokenUtil;

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

}
