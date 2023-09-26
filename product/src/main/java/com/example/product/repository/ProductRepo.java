package com.example.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.product.entity.Product;

public interface ProductRepo extends JpaRepository<Product, Long>{
    @Query("select u from Product u where u.name like %?1% ")
	Page<Product> searchQuery(String search, Pageable pageable);
}
