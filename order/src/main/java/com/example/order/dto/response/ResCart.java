package com.example.order.dto.response;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Data
public class ResCart {
    Long id;
    Timestamp updatedAt;
    Timestamp createdAt;
    Long productId;
    Integer price;
    Integer qty;
}
