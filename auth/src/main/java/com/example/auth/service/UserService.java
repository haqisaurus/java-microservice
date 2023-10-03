package com.example.auth.service;

import java.util.Map;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.auth.config.jwt.JwtTokenUtil;
import com.example.auth.config.jwt.JwtUserDetail;
import com.example.auth.dto.request.ReqLogin;
import com.example.auth.dto.response.ResLogin;
import com.example.auth.dto.response.ResUser;
import com.example.auth.entity.User;
import com.example.auth.entity.UserCompanyRole;
import com.example.auth.exception.ResourceNotFoundException;
import com.example.auth.repository.UserRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ModelMapper modelMapper;


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

    public ResUser getUserDetail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetail userDetails = (JwtUserDetail) authentication.getPrincipal();
        User user = userRepo.findById(userDetails.getId()).orElseThrow(() -> new ResourceNotFoundException("Undefined user"));
        ResUser resUser = modelMapper.map(user, ResUser.class);
        return resUser;
    }
}
