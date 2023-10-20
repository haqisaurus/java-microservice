package com.example.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.dto.request.ReqLogin;
import com.example.auth.dto.request.ReqUser;
import com.example.auth.dto.response.ResLogin;
import com.example.auth.exception.ResourceNotFoundException;
import com.example.auth.service.UserService;

@RestController
public class AuthController {
    @Autowired
    UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

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

        // try {
        // accountService.publishOrderEvent(orderRequest);
        // } catch (JsonProcessingException e) {
        // e.printStackTrace();
        // }
        // int a = 1/0;
        throw new ResourceNotFoundException("Resource with id  ot found");
        // return ResponseEntity.ok("coba secure");
    }
}
