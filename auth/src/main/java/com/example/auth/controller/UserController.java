package com.example.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.config.jwt.JwtUserDetail;
import com.example.auth.dto.response.ResUser;
import com.example.auth.entity.User;
import com.example.auth.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping(value = "/detail")
    public ResponseEntity<?> getCurrentUserDetail() {
        ResUser user = userService.getUserDetail();
        System.out.println(org.hibernate.Version.getVersionString());
        return ResponseEntity.ok(user);
    }
}
