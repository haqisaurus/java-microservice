package com.example.product.dto.request;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.example.product.dto.response.ResProduct;

import lombok.Data;

@Data
public class ReqOrderCanceled {
    Long id;
    Long userId; 
    private List<ReqCanceledProduct> carts = new ArrayList<ReqCanceledProduct>();
    String status;
}
