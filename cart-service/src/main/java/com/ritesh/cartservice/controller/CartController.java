package com.ritesh.cartservice.controller;

import com.ritesh.cartservice.dtos.request.AddToCartRequest;
import com.ritesh.cartservice.dtos.response.CartResponse;
import com.ritesh.cartservice.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Add Product to Cart
    @PostMapping("/{userId}")
    public ResponseEntity<CartResponse> addToCart(
            @PathVariable Long userId,
            @Valid @RequestBody AddToCartRequest request
    ) {

        CartResponse response = cartService.addToCart(userId, request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get User Cart
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(
            @PathVariable Long userId
    ) {

        CartResponse response = cartService.getCart(userId);

        return ResponseEntity.ok(response);
    }

    // Remove Product From Cart
    @DeleteMapping("/{userId}/product/{productId}")
    public ResponseEntity<String> removeProductFromCart(
            @PathVariable Long userId,
            @PathVariable Long productId
    ) {

        cartService.removeProductFromCart(userId, productId);

        return ResponseEntity.ok("Product removed from cart successfully.");
    }

    // Clear Cart
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<String> clearCart(
            @PathVariable Long userId
    ) {

        cartService.clearCart(userId);

        return ResponseEntity.ok("Cart cleared successfully.");
    }
}