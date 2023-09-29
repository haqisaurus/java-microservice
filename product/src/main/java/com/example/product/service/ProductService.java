package com.example.product.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.product.dto.request.ReqProduct;
import com.example.product.dto.response.ResProduct;
import com.example.product.entity.Product;
import com.example.product.exception.ResourceNotFoundException;
import com.example.product.repository.ProductRepo;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductService {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private ModelMapper modelMapper;

    public Page<ResProduct> getProductList(String search, Pageable pageable) {
        log.info("saya terpanggil");
        Page<Product> paged = new PageImpl<>(new ArrayList<>(), pageable, 0);
        if (search == null) {
            paged = productRepo.findAll(pageable);
        } else {
            paged = productRepo.searchQuery(search, pageable);
        }
        Page<ResProduct> newPaged = paged.map(new Function<Product, ResProduct>() {
            @Override
            public ResProduct apply(Product entity) {
                ResProduct dto = modelMapper.map(entity, ResProduct.class);

                return dto;
            }
        });
        return newPaged;
    } 

    public Product getDetail(long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("product Id not found"));
        return product;
    }

    @Transactional
    public Product addProduct(ReqProduct req) {
        Product product = new Product();
        ModelMapper mm = new ModelMapper();
        product = mm.map(req, Product.class);
        productRepo.save(product);

        return product;
    }

    public Object getList() {
        return productRepo.findAll();
    }
}
