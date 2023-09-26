package com.example.order.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.order.dto.request.ReqCart;
import com.example.order.dto.request.ReqCheckout;
import com.example.order.dto.response.ResCommon;
import com.example.order.dto.response.ResOrder;
import com.example.order.entity.Order;
import com.example.order.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/cart-list")
    public ResponseEntity<?> getCartList(HttpServletRequest request,
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC), }) Pageable pageable) {

        Order order = orderService.getOrderChart();
        ModelMapper modelMapper = new ModelMapper();
        ResOrder response = modelMapper.map(order, ResOrder.class);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<?> postAddCart(@RequestBody ReqCart req) {
        orderService.addToChart(req);
        ResCommon response = new ResCommon("Cart added");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/remove-from-cart")
    public ResponseEntity<?> postRemoveCart(@RequestBody ReqCart req) {
        orderService.removeFromChart(req);
        ResCommon response = new ResCommon("Cart removed");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> postCheckout(@RequestBody ReqCheckout req) throws JsonProcessingException {
        orderService.checkout(req);
        ResCommon response = new ResCommon("Checked Out");
        return ResponseEntity.ok(response);
    }
}
