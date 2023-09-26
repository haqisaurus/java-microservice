package com.example.product.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.product.dto.request.ReqProduct;
import com.example.product.entity.Product;
import com.example.product.exception.ResourceNotFoundException;
import com.example.product.repository.ProductRepo;

import jakarta.transaction.Transactional;

@Service
public class ProductService {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepo productRepo;

    public Page<Product> getProductList(Pageable pageable) {
        return productRepo.findAll(pageable);
    }

    public Page<Product> searchQuery(String search, Pageable pageable) {
        return productRepo.searchQuery(search, pageable);
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
