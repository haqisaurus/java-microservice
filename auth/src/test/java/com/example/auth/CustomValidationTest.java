package com.example.auth;

import com.example.auth.config.jwt.JwtTokenUtil;
import com.example.auth.config.jwt.JwtUserDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collection;
import java.util.Collections;


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
    @DisplayName("Test upload file success")
    public void testUploadFileSuccess() throws Exception {
        Resource fileResource = new ClassPathResource("/banner.txt");
        log.info(fileResource.getFilename());
//        java.io.File file = new java.io.File("D:\\experimental\\microservice java\\auth\\src\\main\\resources\\banner.txt");
//        FileInputStream fileInputStream = new FileInputStream(file);
        MockMultipartFile firstFile = new MockMultipartFile("file", fileResource.getFilename(),  MediaType.TEXT_PLAIN_VALUE, fileResource.getInputStream());
//        MockMultipartFile file
//                = new MockMultipartFile(
//                "file",
//                "hello.txt",
//                MediaType.TEXT_PLAIN_VALUE,
//                "Hello, World!".getBytes()
//        );
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.multipart("/user/upload-file")
//                .file("file", firstFile.getBytes())
                .file(firstFile)
                .header("Authorization", token) ;

        mockMvc.perform(request)
                .andExpectAll(MockMvcResultMatchers.status().isOk()) ;
    }

    @Test
    @DisplayName("Test download file success")
    public void testDownloadFileSuccess() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/user/download-file")
                .header("Authorization", token) ;

        mockMvc.perform(request)
                .andExpectAll(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml"));
    }
}
