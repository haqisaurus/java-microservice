package com.example.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public InternalServerException(String message) {
        super(message);
    }
}
