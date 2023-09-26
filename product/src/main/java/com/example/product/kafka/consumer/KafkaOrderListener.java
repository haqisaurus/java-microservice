package com.example.product.kafka.consumer;

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

import com.example.product.dto.request.ReqCanceledProduct;
import com.example.product.dto.request.ReqOrderCanceled;
import com.example.product.dto.response.ResOrder;
import com.example.product.dto.response.ResProduct;
import com.example.product.entity.Product;
import com.example.product.repository.ProductRepo;
import com.example.product.service.kafka.KafkaOrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaOrderListener {

   
    @Autowired
    private KafkaOrderService kafkaOrderService;


    @KafkaListener(topics = "kafka-test", containerFactory = "concurrentKafkaListenerContainerFactory")
    public void processMessage(String message, @Header(KafkaHeaders.RECEIVED_KEY) List<?> partitions, @Header(KafkaHeaders.RECEIVED_TOPIC) List<?> topics, @Header(KafkaHeaders.OFFSET) List<?> offsets) throws JsonProcessingException {
        System.out.println("pesan datang ============================================");
         
        kafkaOrderService.handleOrderListener(message);
    }
}
