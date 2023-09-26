package com.example.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class UnproccesableEntity extends RuntimeException { 
	public UnproccesableEntity(String message) {
		super(message);
	}
}