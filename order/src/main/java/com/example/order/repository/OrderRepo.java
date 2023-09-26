package com.example.order.repository;

import java.util.Optional; 

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.order.entity.Order;

public interface OrderRepo extends JpaRepository<Order, Long> {
    Optional<Order> findByIdAndStatusAndUserId(Long id, String status, Long userId);
    Optional<Order> findByStatusAndUserId(String status, Long userId);
}
