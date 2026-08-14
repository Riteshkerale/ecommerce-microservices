package com.ritesh.orderservice.feign;

import com.ritesh.orderservice.dtos.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("product-service")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ProductResponse getProductById(
            @PathVariable Long id
    );

    @PutMapping("/api/products/{id}/stock")
    void reduceStock(
            @PathVariable Long id,
            @RequestParam int quantity
    );
}