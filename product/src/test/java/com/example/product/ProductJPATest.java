package com.example.product;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import com.example.product.entity.Product;
import com.example.product.repository.ProductRepo;

@ActiveProfiles("test")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductJPATest {
    @Autowired
    private ProductRepo productRepo;

    @BeforeAll
    public void setupDB() {
        productRepo.deleteAll();
    }

    // @BeforeEach
    // public void setup() {
    //     productRepo.deleteAll();
    // }

    @Test
    @Sql(scripts = { "classpath:insert-product.sql" })
    void whenInsertProduct() {
        Optional<Product> user = productRepo.findById(3L);
        Assertions.assertThat(user).isNotEmpty();

    }

    @Test 
    void countTest() {
        List<Product> list = productRepo.findAll();
        Assertions.assertThat(list.size()).isEqualTo(3);

    }
}
