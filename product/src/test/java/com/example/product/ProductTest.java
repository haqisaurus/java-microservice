package com.example.product;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import org.assertj.core.util.DateUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.product.config.jwt.JwtTokenUtil;
import com.example.product.config.jwt.JwtUserDetail;
import com.example.product.controller.ProductController;
import com.example.product.dto.request.ReqProduct;
import com.example.product.dto.response.ResProduct;
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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductTest {
        private static final Logger log = org.slf4j.LoggerFactory.getLogger(ProductTest.class);
        @Autowired
        private MockMvc mockMvc;
        @Autowired
        KafkaOrderService kafkaOrderService;

        @Autowired
        private ObjectMapper objectMapper;
        @Autowired
        JwtTokenUtil tokenUtil;
        // bagian ini untuk test dari db langusng
        @Mock
        private ProductRepo productRepo;
        @InjectMocks
        private ProductService productServiceDb;
        // e: bagian ini untuk test dari db langusng
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
        public void testDeleteProductSuccess() throws Exception {
                MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/delete-product/{id}", 1L)
                                .characterEncoding("utf-8")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON);

                MvcResult mvcResult = mockMvc.perform(request)
                                .andExpect(MockMvcResultMatchers.status().isNoContent())
                                .andDo(MockMvcResultHandlers.print())
                                .andReturn();

                String actualResponseBody = mvcResult.getResponse().getContentAsString();
                log.info(actualResponseBody);
        }

        @Test
        public void testDeleteProductNotFound() throws Exception {
                Assertions.assertDoesNotThrow(() -> productService.deleteProduct(11L));
                MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/delete-product/{id}", 11L)
                                .characterEncoding("utf-8")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON);

                MvcResult mvcResult = mockMvc.perform(request)
                                .andExpect(MockMvcResultMatchers.status().isNoContent()) 
                                .andDo(MockMvcResultHandlers.print())
                                .andReturn();

                String actualResponseBody = mvcResult.getResponse().getContentAsString();
                log.info(actualResponseBody); 
        }

        @Test
        public void testUpdateProduct() throws Exception {
                ReqProduct req = ReqProduct.builder().name("nama").qty(12).price(1000).build();
                ResProduct res = ResProduct.builder().id(1L).name("gelas").qty(12).price(1000).build();
                when(productService.updateProduct(res.getId(), req)).thenReturn(res);
                String payload = objectMapper.writeValueAsString(req);
                log.info(payload);
                MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/update-product/" + res.getId())
                                .characterEncoding("utf-8")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(payload);

                MvcResult mvcResult = mockMvc.perform(request)
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                // .andExpect(MockMvcResultMatchers.jsonPath("$.number").value(0))
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("gelas"))
                                .andDo(MockMvcResultHandlers.print())
                                .andReturn();
                String actualResponseBody = mvcResult.getResponse().getContentAsString();
                log.info(actualResponseBody);

        }

        @Test
        public void testProductPage() throws Exception {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.ssXXX");
                Date parsedDate = dateFormat.parse("2023-09-26T08:43:00.178+00:00");
                Timestamp update = new java.sql.Timestamp(parsedDate.getTime());

                parsedDate = dateFormat.parse("2023-09-26T08:43:00.177+00:00");
                Timestamp create = new java.sql.Timestamp(parsedDate.getTime());
                ResProduct r1 = ResProduct.builder().id(3L).name("gelas").qty(3).price(1000).productId(3L)
                                .updatedAt(update).createdAt(create).build();

                PageRequest pagination = PageRequest.of(0, 1, Direction.DESC, "id");
                List<ResProduct> productList = Arrays.asList(r1);
                Page<ResProduct> productPage = new PageImpl<>(productList, pagination, productList.size());
                // when(productService.getProductList(pagination)).thenReturn(productPage);

                // test langsung dari database
                Product rdb1 = new Product();
                rdb1.setId(3L);
                rdb1.setName("gelasd");
                rdb1.setQty(3);
                rdb1.setPrice(1000);
                rdb1.setUpdatedAt(update);
                rdb1.setCreatedAt(create);
                List<Product> productListDb = Arrays.asList(rdb1);
                Page<Product> productPageDb = new PageImpl<>(productListDb, pagination,
                                productList.size());

                Page<Product> whencall = productRepo.findAll(pagination);
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
                                .andExpect(jsonPath("$.content[*].id").value(3))
                                .andExpect(jsonPath("$.content[*].name").value("gelas"))
                                .andDo(MockMvcResultHandlers.print())
                                .andReturn();
                String actualResponseBody = mvcResult.getResponse().getContentAsString();
                log.info(actualResponseBody);

                ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
                verify(productService).getProductList("", pageableCaptor.capture());
                PageRequest pageable = (PageRequest) pageableCaptor.getValue();
                log.info(pageable.toString());

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
                assertDoesNotThrow(() -> {
                        String message = "{\"id\":2,\"updatedAt\":1695720243935,\"createdAt\":1695720243934,\"userId\":1,\"carts\":[{\"id\":2,\"updatedAt\":1695720293122,\"createdAt\":1695720293122,\"productId\":1,\"name\":null,\"qty\":1000,\"price\":1000}],\"status\":\"DRAFT\"}";

                        kafkaOrderService.handleOrderListener(message);
                });
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
