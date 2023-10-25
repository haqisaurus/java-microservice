package com.example.auth.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtEntryPoint implements AuthenticationEntryPoint {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(JwtEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        log.error("Unauthorized error: {}", authException.getMessage());
        Map<String, Object> errorObject = new HashMap<>();
        int errorCode = 401;
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

        errorObject.put("message", authException.getMessage());
        errorObject.put("code", errorCode);
        errorObject.put("detail", "Error: Unauthorized");
        errorObject.put("timestamp", timeStamp);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode);
        ObjectMapper a = new ObjectMapper();

        response.getWriter().write(a.writeValueAsString(errorObject));
    }
}