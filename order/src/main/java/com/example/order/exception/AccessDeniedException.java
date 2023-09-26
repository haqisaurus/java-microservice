package com.example.order.exception; 

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class AccessDeniedException extends org.springframework.security.access.AccessDeniedException {
    private static final long serialVersionUID = 1L;
    
    public AccessDeniedException(String message) {
        super(message);
    }
}