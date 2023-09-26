package com.example.order.kafka.consumer;

 

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.example.order.dto.response.ResCanceledProduct;
import com.example.order.dto.response.ResOrderCanceled;
import com.example.order.entity.Order;
import com.example.order.repository.OrderRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;

@Service
public class KafkaOrderConsumer {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(KafkaOrderConsumer.class);

    @Autowired
    private OrderRepo orderRepo;

    @KafkaListener(topics = "order-canceled", containerFactory = "concurrentKafkaListenerContainerFactory")
    public void processMessage(String message, @Header(KafkaHeaders.RECEIVED_KEY) List<?> partitions,
            @Header(KafkaHeaders.RECEIVED_TOPIC) List<?> topics, @Header(KafkaHeaders.OFFSET) List<?> offsets) {
        System.out.println("pesan datang ============================================");
        // message = message.substring(1, message.length() - 1);
        // System.out.println(message);
        ObjectMapper objectMapper = new ObjectMapper();
        ResOrderCanceled cancelOrder = new ResOrderCanceled();
        UUID uuid = UUID.randomUUID();
        try {
            cancelOrder = objectMapper.readValue(message, ResOrderCanceled.class);

            Order order = orderRepo.findById(cancelOrder.getId())
                    .orElseThrow(() -> new EntityNotFoundException("order not found"));
            order.setStatus("PAYMENT_CANCELED");
            orderRepo.save(order);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
