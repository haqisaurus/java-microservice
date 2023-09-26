package com.example.order.config.jwt.filterrequest;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.order.config.jwt.JwtTokenUtil;
import com.example.order.config.jwt.JwtUserDetail;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String incomingRequest = " REQUEST ##### " + request.getRemoteAddr() + " <==> " + request.getMethod()
                + " path ===> " + request.getRequestURI() + " :: size " + (request.getContentLength()) + " b :: type "
                + request.getContentType();
        logger.info(incomingRequest);

        String authHeader = request.getHeader("Authorization");
        String token = null;
        Long userId = 0L;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            userId = jwtTokenUtil.getUserIdFromToken(token);
        }

        if (userId != 0 && SecurityContextHolder.getContext().getAuthentication() == null) {
            Claims claims = jwtTokenUtil.getClaims(token);
            String username = claims.get("username").toString();
            String firstName = claims.get("firstName").toString();
            String lastName = claims.get("lastName").toString();
            Collection<GrantedAuthority> authorities = Collections.emptySet();
            JwtUserDetail userDetails = new JwtUserDetail(userId, username, token, firstName, lastName, authorities);
            if (jwtTokenUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info(userDetails.getId());
            }
        }
        filterChain.doFilter(request, response);
    }

}
