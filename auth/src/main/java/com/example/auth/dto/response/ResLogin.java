package com.example.auth.dto.response;

import lombok.Data;

@Data
public class ResLogin {
    String token;
    String refreshToken;
    long expired;
}
