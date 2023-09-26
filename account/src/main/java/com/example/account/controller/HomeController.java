package com.example.account.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
import com.example.account.dto.OrderRequest; 
import com.example.account.service.AccountService;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/api/v1")
public class HomeController {
    
    @Autowired
    private AccountService accountService;
    @PostMapping(value = "/test")
    public ResponseEntity<?> halo(@RequestBody OrderRequest orderRequest) {
          
        // try {
        //     accountService.publishOrderEvent(orderRequest);
        // } catch (JsonProcessingException e) { 
        //     e.printStackTrace();
        // }
        return ResponseEntity.ok("hai");
    }
}
