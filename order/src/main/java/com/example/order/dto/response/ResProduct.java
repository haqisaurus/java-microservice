package com.example.order.dto.response;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Data
public class ResProduct {
    Long id; 
    Timestamp updatedAt; 
    Timestamp createdAt;
    Long productId; 
    String name;
    Integer qty;
    Integer price;
}
