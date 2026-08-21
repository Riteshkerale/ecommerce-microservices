package com.ritesh.orderservice.service;

import com.ritesh.orderservice.dtos.response.ProductResponse;
import com.ritesh.orderservice.feign.ProductClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Service
@RequiredArgsConstructor
public class ProductServiceClientService {

    private final ProductClient productClient;

    @CircuitBreaker(
            name = "productService",
            fallbackMethod = "getProductFallback"
    )
    @Retry(name = "productServiceRetry")
    @TimeLimiter(
            name = "productServiceTimeLimiter"
    )
    public CompletionStage<ProductResponse> getProductWithRetry(Long productId) {

        System.out.println("Calling Product Service - getProductById");

        return CompletableFuture.supplyAsync(
                () -> productClient.getProductById(productId)
        );
    }


    @CircuitBreaker(
            name = "productService",
            fallbackMethod = "reduceStockFallback"
    )
    @Retry(name = "productServiceRetry")
    @TimeLimiter(
            name = "productServiceTimeLimiter"
    )
    public CompletionStage<Void> reduceStockWithRetry(
            Long productId,
            int quantity
    ) {

        System.out.println("Calling Product Service - reduceStock");

        return CompletableFuture.runAsync(
                () -> productClient.reduceStock(productId, quantity)
        );
    }


    public CompletionStage<ProductResponse> getProductFallback(
            Long productId,
            Throwable throwable
    ) {

        System.out.println(
                "Circuit Breaker / TimeLimiter - Product Service unavailable"
        );

        return CompletableFuture.failedFuture(
                new RuntimeException(
                        "Product Service is currently unavailable. Please try again later."
                )
        );
    }


    public CompletionStage<Void> reduceStockFallback(
            Long productId,
            int quantity,
            Throwable throwable
    ) {

        System.out.println(
                "Circuit Breaker / TimeLimiter - Unable to reduce stock"
        );

        return CompletableFuture.failedFuture(
                new RuntimeException(
                        "Product Service is currently unavailable. Unable to reduce stock."
                )
        );
    }
}