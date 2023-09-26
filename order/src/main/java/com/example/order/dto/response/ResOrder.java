package com.example.order.dto.response;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResOrder {
    Long id;
    Timestamp updatedAt;
    Timestamp createdAt;
    Long userId; 
    private List<ResProduct> carts = new ArrayList<ResProduct>();
    String status;
}