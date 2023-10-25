package com.example.auth.exception;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,@Nullable HttpHeaders headers,@Nullable HttpStatusCode status, @Nullable WebRequest request) {

        Map<String, Map<String, String>> body = new HashMap<>();

//        List<String> errors = ex.getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .map(DefaultMessageSourceResolvable::getDefaultMessage)
//                .collect(Collectors.toList());
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errorMap.put(error.getField(), error.getDefaultMessage()));

        body.put("errors", errorMap);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,@Nullable HttpHeaders headers,@Nullable HttpStatusCode status, @Nullable WebRequest request) {
        ApiError apiError = new ApiError();
        apiError.setCode(2001);
        apiError.setMessage(ex.getMessage());
        apiError.setDetails(ex.getLocalizedMessage());
        apiError.setTimestamp(new Date());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNoSuchElementFoundException(ResourceNotFoundException ex) {
        log.error("Failed to find the requested element", ex);
        ApiError apiError = new ApiError();
        apiError.setCode(2001);
        apiError.setMessage(ex.getMessage());
        apiError.setDetails(ex.getLocalizedMessage());
        apiError.setTimestamp(new Date());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    // handling global exception
    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<?> globalExceptionHandling(InternalServerException exception) {
        log.error("ada error 500", exception);

        ApiError apiError = new ApiError();
        apiError.setCode(2001);
        apiError.setMessage(exception.getMessage());
        apiError.setDetails(exception.getLocalizedMessage());
        apiError.setTimestamp(new Date());

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }



}
