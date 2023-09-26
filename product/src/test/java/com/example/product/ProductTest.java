package com.example.product;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.product.config.jwt.JwtTokenUtil;
import com.example.product.config.jwt.JwtUserDetail;
import com.example.product.controller.ProductController;
import com.example.product.dto.request.ReqProduct;
import com.example.product.entity.Product;
import com.example.product.repository.ProductRepo;
import com.example.product.service.ProductService;
import com.example.product.service.kafka.KafkaOrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.hamcrest.Matchers.hasSize;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ProductTest {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ProductTest.class);
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    KafkaOrderService kafkaOrderService;
    
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    JwtTokenUtil tokenUtil;
    @MockBean
    private ProductService productService;
    private Product product;
    private String token;

    @BeforeEach
    public void setup() {
        product = new Product();
        Collection<GrantedAuthority> authorities = Collections.emptySet();
        JwtUserDetail userDetail = new JwtUserDetail(1L, "lala", "password", "fir", "la", authorities);
        Map<String, Object> claims = tokenUtil.generateUserToken(userDetail);
        log.info(claims.get("token").toString());
        token = "Bearer " + claims.get("token").toString();
    }

    @Test
    public void testGetOrdersList() throws Exception {
         

        // when(something happens).thenReturn(do something)
        when(productService.getList()).thenReturn(List.of(new Product(1L, null, null, "gelas", 1, 1)));

        mockMvc.perform(MockMvcRequestBuilders.get("/product-list")
                .header("Authorization", token))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk()) 
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("gelas")) ;
    }

    @Test
    public void testConsumer() {
        assertDoesNotThrow(() -> kafkaOrderService.handleOrderListener(
                "{\"id\":1,\"updatedAt\":1695351885119,\"createdAt\":1695279058383,\"userId\":1,\"carts\":[{\"id\":4,\"updatedAt\":1695346571015,\"createdAt\":1695346326507,\"name\":null,\"qty\":9,\"price\":1000},{\"id\":5,\"updatedAt\":1695346671479,\"createdAt\":1695346658448,\"name\":null,\"qty\":2,\"price\":1000}],\"status\":\"DRAFT\"}"));
    }

   
    @Test
    public void addProductReturnTrue() throws JsonProcessingException, Exception {
        // given
        Product newProduct = Product.builder()
                .id(1L)
                .name("gelas")
                .price(1000)
                .qty(3)
                .build();
        ReqProduct req = ReqProduct.builder()
                .name("gelas")
                .price(1000)
                .qty(3)
                .build();
        when(productService.addProduct(req)).thenReturn(newProduct);

        String payload = objectMapper.writeValueAsString(newProduct);
        log.info(payload);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/add-product")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(payload);

        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(newProduct.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        log.info(actualResponseBody);
    }

    @Test
    public void getDetailProduct() throws Exception {
        Product product = Product.builder()
                .id(1L)
                .name("gelas")
                .price(1000)
                .qty(3)
                .build();

        when(productService.getDetail(1L)).thenReturn(product);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/{id}", product.getId())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(product.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        log.info(actualResponseBody);
    }
}
