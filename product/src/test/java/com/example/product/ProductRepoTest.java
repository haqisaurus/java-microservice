package com.example.product;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.product.config.jwt.JwtTokenUtil;
import com.example.product.config.jwt.JwtUserDetail;
import com.example.product.dto.response.ResProduct;
import com.example.product.entity.Product;
import com.example.product.repository.ProductRepo;
import com.example.product.service.ProductService;

import lombok.extern.slf4j.Slf4j;
 
@SpringBootTest
@Slf4j
@AutoConfigureMockMvc(addFilters = false)
public class ProductRepoTest {

    @Autowired
    private MockMvc mockMvc;
    // cut prosesnya langsung dari reposidoty 
    @Mock
    private ProductRepo productRepo;
    @InjectMocks
    private ProductService productService;
    //e: cut prosesnya langsung dari reposidoty 
    @Autowired    
    JwtTokenUtil tokenUtil;
    private String token;

    @BeforeEach
    public void setup() {

        Collection<GrantedAuthority> authorities = Collections.emptySet();
        JwtUserDetail userDetail = new JwtUserDetail(1L, "lala", "password", "fir", "la", authorities);
        Map<String, Object> claims = tokenUtil.generateUserToken(userDetail);
        log.info(claims.get("token").toString());
        this.token = "Bearer " + claims.get("token").toString();
    }

    @Test
    @DisplayName("Operasi test dari repository product")
    public void testProductPage() throws Exception {
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.ssXXX");
        Date parsedDate = dateFormat.parse("2023-09-26T08:43:00.178+00:00");
        Timestamp update = new java.sql.Timestamp(parsedDate.getTime()); 
        parsedDate = dateFormat.parse("2023-09-26T08:43:00.177+00:00");
        Timestamp create = new java.sql.Timestamp(parsedDate.getTime());
       
        PageRequest pagination = PageRequest.of(0, 1, Direction.DESC, "id");
       
        // test langsung dari database
        Product rdb1 = new Product();
        

        List<Product> productListDb = Arrays.asList(rdb1);
        Page<Product> productPageDb = new PageImpl<>(productListDb, pagination, productListDb.size());
        // karena langsung ambil dari database jadi tidak perlu init rdb1 tidak ada data
        when(productRepo.findAll(pagination)).thenReturn(productPageDb);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/list")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("page", "0")
                .param("size", "1")
                .param("sort", "id,desc");

        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                // .andExpect(MockMvcResultMatchers.jsonPath("$.number").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].name").value("gelasd"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        log.info(actualResponseBody);
        Assertions.assertThat("a").contains("a");

    }

}
