package com.example.auth.controller;

import com.example.auth.dto.request.ReqLogin;
import com.example.auth.dto.request.ReqUser;
import com.example.auth.dto.response.ResLogin;
import com.example.auth.exception.ResourceNotFoundException;
import com.example.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> postLogin(@RequestBody ReqLogin req) {
        ResLogin response = userService.performLogin(req);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/login")
    public ResponseEntity<?> getLogin() {
        return ResponseEntity.ok("response");
    }

    @GetMapping(value = "/pass")
    public ResponseEntity<?> getPass() {
        return ResponseEntity.ok(passwordEncoder.encode("password"));
    }

    @PostMapping(value = "/update")
    public ResponseEntity<?> postUpdateUser(@RequestBody ReqUser req) {
        userService.updateUser(req);
        return ResponseEntity.ok("OK");
    }

    @GetMapping(value = "/secure")
    public ResponseEntity<?> secure() {
        throw new ResourceNotFoundException("Resource with id  not found");
    }
}
