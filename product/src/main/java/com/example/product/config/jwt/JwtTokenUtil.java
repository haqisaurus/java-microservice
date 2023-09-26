package com.example.product.config.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiredInDays}")
    private int expiredInDays; 

    // generate token for user
    public Map<String, Object> generateUserToken(JwtUserDetail user) {
        Long currentTimeMillis = System.currentTimeMillis();
        Date expired = new Date(currentTimeMillis + ((24 * 60 * 60 * 1000) * expiredInDays));
        Map<String, Object> claims = new HashMap<>();
        Map<String, Object> result = new HashMap<>();
        claims.put("userId", user.getId());  
        claims.put("username", user.getUsername());  
        claims.put("firstName", user.getFirstName());  
        claims.put("lastName", user.getLastName());  
        String token =  Jwts.builder()
                .setClaims(claims)
                .setSubject("userName")
                .setIssuedAt(new Date(currentTimeMillis))
                .setExpiration(expired)
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
        result.put("token", token);
        result.put("expired", expired.getTime());
        return result;
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret); 
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // retrieve username from jwt token
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        try {
            String userId = claims.get("userId").toString();
            return Long.parseLong(userId);
        } catch (Exception e) {
            return null;
        }
    }

    public Long getIssuedAt(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        try {
            String userId = claims.get("iat").toString();
            return Long.parseLong(userId);
        } catch (Exception e) {
            return null;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, JwtUserDetail userDetails) {
        final Long userId = getUserIdFromToken(token);
        return (userId.equals(userDetails.getId()) && !isTokenExpired(token));
    }

}
