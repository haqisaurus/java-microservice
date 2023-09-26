package com.example.account.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.account.dto.OrderRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; 
 
@Service
public class AccountService {
    // @Autowired
    // private Sinks.Many<OrderEvent> orderSinks;

    @Autowired 
    private  KafkaTemplate<String, String> kafkaTemplate;

    public void publishOrderEvent(OrderRequest orderEvent ) throws JsonProcessingException{
        // OrderEvent orderEvent = new OrderEvent(orderEventDto);
        // orderSinks.tryEmitNext(orderEvent);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(orderEvent);
        kafkaTemplate.send("order-event", jsonString);

    }
}

 