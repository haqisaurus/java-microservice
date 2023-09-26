package com.example.order.config.jwt;

import java.io.IOException;
import java.io.Serializable;
import java.security.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtEntryPoint implements AuthenticationEntryPoint {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(JwtEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {
        log.error("Unauthorized error: {}", authException.getMessage());
        Map<String, Object> errorObject = new HashMap<>();
        int errorCode = 401;
        errorObject.put("message", authException.getMessage());
        errorObject.put("code", errorCode);
        errorObject.put("detail", "Error: Unauthorized");
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode);
        ObjectMapper a = new ObjectMapper();
        response.getWriter().write(a.writeValueAsString(errorObject));
        // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error:
        // Unauthorized");
    }
}