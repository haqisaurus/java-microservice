package com.example.auth.dto.request;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReqLogin {
    String username;
    String password;
}
