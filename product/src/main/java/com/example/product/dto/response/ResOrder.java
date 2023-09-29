package com.example.product.dto.response;



import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) 
public class ResOrder {
    Long id;
    Timestamp updatedAt;
    Timestamp createdAt;
    Long userId; 
    private List<ResProduct> carts;
    String status;
}
