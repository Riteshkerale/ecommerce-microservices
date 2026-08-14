package com.ritesh.orderservice.controller;

import com.ritesh.orderservice.dtos.response.OrderResponse;
import com.ritesh.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/users/{userId}")
    public ResponseEntity<OrderResponse> placeOrder(
            @PathVariable Long userId) {

        OrderResponse response = orderService.placeOrder(userId);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

}