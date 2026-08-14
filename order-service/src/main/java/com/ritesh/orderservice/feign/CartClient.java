package com.ritesh.orderservice.feign;

import com.ritesh.orderservice.dtos.response.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

@FeignClient(name = "cart-service")
public interface CartClient {

    @GetMapping("/api/cart/{userId}")
    CartResponse getCart(@PathVariable Long userId);

    @DeleteMapping("/api/cart/{userId}/clear")
    void clearCart(@PathVariable Long userId);
}