package com.example.auth.service;

import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.auth.config.jwt.JwtTokenUtil;
import com.example.auth.config.jwt.JwtUserDetail;
import com.example.auth.dto.request.ReqLogin;
import com.example.auth.dto.response.ResLogin;

@Service
public class UserService {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(UserService.class);
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public ResLogin performLogin(ReqLogin req) {
        ResLogin response = new ResLogin();

        // lakukan authenticate
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        // jika berhasil generate token
        if (authentication.isAuthenticated()) {
            JwtUserDetail user = (JwtUserDetail) authentication.getPrincipal();

            Map<String, Object> token = jwtTokenUtil.generateUserToken(user);
            response.setToken(token.get("token").toString());
            response.setExpired(Long.parseLong(token.get("expired").toString()));
        } else {
            // throw new UsernameNotFoundException("invalid user request !");
            response.setToken("error bos");

        }
        return response;
    }
}
