package com.example.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.order.entity.Cart;
import com.example.order.entity.Order;

public interface CartRepo extends JpaRepository<Cart, Long> {

    Optional<Cart> findTopByOrderAndProductId(Order order, Long id);

}
