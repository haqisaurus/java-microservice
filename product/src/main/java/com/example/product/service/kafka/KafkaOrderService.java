package com.example.product.service.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.product.dto.request.ReqCanceledProduct;
import com.example.product.dto.request.ReqOrderCanceled;
import com.example.product.dto.response.ResOrder;
import com.example.product.entity.Product;
import com.example.product.kafka.consumer.KafkaOrderListener;
import com.example.product.repository.ProductRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaOrderService {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(KafkaOrderService.class);
    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private ProductRepo productRepo;

    public void handleOrderListener(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ResOrder order = new ResOrder();

        order = objectMapper.readValue(message, ResOrder.class);

        log.info(order.getCarts().toString());
        // check quantity
        List<ReqCanceledProduct> errorProducts = new ArrayList<ReqCanceledProduct>();
        order.getCarts().stream().forEach(v -> {
            Optional<Product> checkProduct = productRepo.findById(v.getProductId());
            // jika barang tidak ada
            if (checkProduct.isEmpty()) {
                ReqCanceledProduct product = new ReqCanceledProduct();
                product.setProductId(v.getProductId());
                product.setReaseon("Product not found");
                errorProducts.add(product);
                // jika stok tidak mencukupi
            } else if (checkProduct.get().getQty() < v.getQty()) {
                ReqCanceledProduct product = new ReqCanceledProduct();
                product.setProductId(v.getProductId());
                product.setReaseon("Insuffience stock");
                errorProducts.add(product);
            }
        });

        if (errorProducts.size() > 0) {
            // sendback error
            ReqOrderCanceled orderCanceled = new ReqOrderCanceled();
            orderCanceled.setUserId(order.getUserId());
            orderCanceled.setId(order.getId());
            orderCanceled.setStatus("PAYMENT_CANCELED");
            orderCanceled.setCarts(errorProducts);
            String payload = objectMapper.writeValueAsString(orderCanceled);
            log.info(payload);
            UUID uuid = UUID.randomUUID();
            kafkaTemplate.send("order-canceled", uuid.toString(), payload);
        }

    }
}
