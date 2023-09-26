package com.example.product.dto.response;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) 
public class ResProduct {
    Long id;
    Timestamp updatedAt;
    Timestamp createdAt;
    Long productId;

    String name;
    Integer qty;
    Integer price;
}
