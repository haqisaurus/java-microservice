package com.example.order.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) 

public class ResCanceledProduct {
    Long productId;
    String reaseon;
}
