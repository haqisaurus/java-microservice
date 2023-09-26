package com.example.order.dto.response;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.example.order.dto.response.ResProduct;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) 

public class ResOrderCanceled {
    Long id;
    Long userId;
    private List<ResCanceledProduct> carts = new ArrayList<ResCanceledProduct>();
    String status;
}
