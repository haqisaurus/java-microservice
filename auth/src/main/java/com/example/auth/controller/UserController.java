package com.example.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.config.jwt.JwtUserDetail;
import com.example.auth.dto.SearchUser;
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

    @GetMapping(value = "/search")
    public ResponseEntity<?> getSEarchUser(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "username", required = false) String username, 
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size
            ) {
        SearchUser searchParams = SearchUser.builder().name(name).username(username).page(page).size(size).build();
        Page<ResUser> contactResponses = (Page<ResUser>) userService.searchUser(searchParams);
        return ResponseEntity.ok(contactResponses);
    }
}
