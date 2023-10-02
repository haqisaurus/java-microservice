package com.example.product.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.product.dto.request.ReqProduct;
import com.example.product.dto.response.ResCommon;
import com.example.product.dto.response.ResOrder;
import com.example.product.dto.response.ResProduct;
import com.example.product.entity.Product;
import com.example.product.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(value = "/add-product", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addProduct(@Valid @RequestBody ReqProduct req, BindingResult result) {
        System.out.println(req.getName());
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getAllErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                String message = error.getDefaultMessage();
                errors.put(fieldName, message);
            });
            return new ResponseEntity<>(errors, null, HttpStatus.BAD_REQUEST);

        }
        log.info(req.toString());
        // add to servci
        Product product = productService.addProduct(req);
        return new ResponseEntity<Product>(product, null, HttpStatus.CREATED);
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProducts(HttpServletRequest request,
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC), }) Pageable pageable) {
        // Authentication authentication =
        // SecurityContextHolder.getContext().getAuthentication();
        // JwtUserDetail userDetails = (JwtUserDetail) authentication.getPrincipal();
        String search = request.getParameter("search");
        Page<ResProduct> list = productService.getProductList(search, pageable);

        return ResponseEntity.ok(list);
    }

    @GetMapping(value = "/product-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductLists() {
        // Authentication authentication =
        // SecurityContextHolder.getContext().getAuthentication();
        // JwtUserDetail userDetails = (JwtUserDetail) authentication.getPrincipal();

        return ResponseEntity.ok(productService.getList());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getDetail(@PathVariable("id") long id) {
        Product product = productService.getDetail(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping(value = "/test")
    public ResponseEntity<?> getDetailtest() {
        ObjectMapper objectMapper = new ObjectMapper();
        String message = "{\"id\":1,\"updatedAt\":1695351885119,\"createdAt\":1695279058383,\"userId\":1,\"carts\":[{\"id\":4,\"updatedAt\":1695346571015,\"createdAt\":1695346326507,\"name\":null,\"qty\":9,\"price\":1000},{\"id\":5,\"updatedAt\":1695346671479,\"createdAt\":1695346658448,\"name\":null,\"qty\":2,\"price\":1000}],\"status\":\"DRAFT\"}";
        ResOrder order = new ResOrder();
        try {
            order = objectMapper.readValue(message, ResOrder.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(order);
    }

    @PutMapping(value = "/update-product/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProduct(@Valid @RequestBody ReqProduct req, BindingResult result,
            @PathVariable("id") long id) {
        ResProduct response = productService.updateProduct(id, req);
        return ResponseEntity.ok(response);

    }

    @DeleteMapping(value = "/delete-product/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteProduct(@PathVariable("id") long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(null, null, HttpStatus.NO_CONTENT);

    }

}
