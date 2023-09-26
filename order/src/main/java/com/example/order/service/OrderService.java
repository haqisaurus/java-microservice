package com.example.order.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.order.config.jwt.JwtUserDetail;
import com.example.order.dto.request.ReqCart;
import com.example.order.dto.request.ReqCheckout;
import com.example.order.dto.response.ResOrder;
import com.example.order.dto.response.ResProduct;
import com.example.order.entity.Cart;
import com.example.order.entity.Order;
import com.example.order.exception.UnproccesableEntity;
import com.example.order.repository.CartRepo;
import com.example.order.repository.OrderRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class OrderService {
    @Autowired
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private CartRepo cartRepo;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public Order getOrderChart() {
        Order order = new Order();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetail userDetails = (JwtUserDetail) authentication.getPrincipal();
        Optional<Order> orderExist = orderRepo.findByStatusAndUserId("DRAFT", userDetails.getId());
        if (orderExist.isEmpty()) {
            order.setStatus("DRAFT");
            order.setUserId(userDetails.getId());
            orderRepo.save(order);
        } else {
            order = orderExist.get();
        }
        return order;
    }

    @Transactional
    public void addToChart(ReqCart req) {
        // cek untuk detail product
        log.info(req.toString());
        RestTemplate restTemplate = new RestTemplate();
        String token = request.getHeader("Authorization");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        ResProduct responseEntity = restTemplate.exchange("http://localhost:8082/product/" + req.getProductId(),
                HttpMethod.GET, entity, ResProduct.class).getBody();
        log.info(responseEntity.toString());
        Order order = getOrderChart();
        // search cart if exist quantity plus 1
        Optional<Cart> cartCheck = cartRepo.findTopByOrderAndProductId(order, responseEntity.getId());
        Cart cart = new Cart();
        if (cartCheck.isPresent()) {
            cart = cartCheck.get();
            int newQty = cart.getQty() + 1;
            if (newQty > responseEntity.getQty()) {
                throw new UnproccesableEntity("Insuffience stock limited");
            }
            cart.setQty(newQty);
            cart.setPrice(responseEntity.getPrice());
        } else {
            cart.setOrder(order);
            cart.setProductId(responseEntity.getId());
            cart.setPrice(responseEntity.getPrice());
            cart.setQty(1);
        }
        cartRepo.save(cart);
        // update total price
        int sum = order.getCarts().stream().mapToInt(v -> {
            return v.getPrice() * v.getQty();
        }).sum();
        order.setTotal(sum);
        orderRepo.save(order);
    }

    @Transactional
    public void removeFromChart(ReqCart req) {
        // cek untuk detail product
        log.info(req.toString());
        RestTemplate restTemplate = new RestTemplate();
        String token = request.getHeader("Authorization");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        ResProduct responseEntity = restTemplate.exchange("http://localhost:8082/product/" + req.getProductId(),
                HttpMethod.GET, entity, ResProduct.class).getBody();
        log.info(responseEntity.toString());
        Order order = getOrderChart();
        // search cart if exist quantity plus 1
        Optional<Cart> cartCheck = cartRepo.findTopByOrderAndProductId(order, responseEntity.getId());
        Cart cart = new Cart();
        if (cartCheck.isPresent()) {
            cart = cartCheck.get();
            int newQty = cart.getQty() - 1;
            if (newQty == 0) {
                cartRepo.delete(cart);
            } else {
                cart.setQty(newQty);
                cart.setPrice(responseEntity.getPrice());
                cartRepo.save(cart);
            }
        }
        // update total price
        int sum = order.getCarts().stream().mapToInt(v -> {
            return v.getPrice() * v.getQty();
        }).sum();
        order.setTotal(sum);
        orderRepo.save(order);
    }

    @Transactional
    public void checkout(ReqCheckout req) throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetail userDetails = (JwtUserDetail) authentication.getPrincipal();
        Optional<Order> orderExist = orderRepo.findByIdAndStatusAndUserId(req.getOrderId(), "DRAFT",
                userDetails.getId());
        if (orderExist.isPresent() && orderExist.get().getCarts().size() > 0) {
            Order order = orderExist.get();
            ModelMapper mm = new ModelMapper();
            ResOrder orderDTO = mm.map(order, ResOrder.class);
            // check all item is exist on product service
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(orderDTO);
            System.out.println(jsonString);
            UUID uuid = UUID.randomUUID(); 
            kafkaTemplate.send("kafka-test", uuid.toString(), jsonString);
            // then update status
            // order.setStatus("PAYMENT");
            // orderRepo.save(order);
        } else {
            throw new UnproccesableEntity("Cart list is empty");

        }
    }

}
