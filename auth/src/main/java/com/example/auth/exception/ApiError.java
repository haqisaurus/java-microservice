package com.example.auth.exception;

import lombok.Data;

import java.util.Date;

@Data
public class ApiError {
    private Date timestamp;
    private String message;
    private String details;
    private Integer code;

    public ApiError( ) {
		super();

	}

}
