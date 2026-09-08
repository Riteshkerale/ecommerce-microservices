package com.ritesh.cartservice.controller;

import com.ritesh.cartservice.dtos.request.AddToCartRequest;
import com.ritesh.cartservice.dtos.response.CartResponse;
import com.ritesh.cartservice.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Add Product to Cart
//    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal")
    @PostMapping("/{userId}")
    public ResponseEntity<CartResponse> addToCart(
            @PathVariable Long userId,
            @Valid @RequestBody AddToCartRequest request
    ) {

        CartResponse response = cartService.addToCart(userId, request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get User Cart
//    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal")
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(
            @PathVariable Long userId
    ) {

        CartResponse response = cartService.getCart(userId);

        return ResponseEntity.ok(response);
    }

    // Remove Product From Cart
//    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal")
    @DeleteMapping("/{userId}/product/{productId}")
    public ResponseEntity<String> removeProductFromCart(
            @PathVariable Long userId,
            @PathVariable Long productId
    ) {

        cartService.removeProductFromCart(userId, productId);

        return ResponseEntity.ok("Product removed from cart successfully.");
    }

    // Clear Cart
//    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal")
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<String> clearCart(
            @PathVariable Long userId
    ) {

        cartService.clearCart(userId);

        return ResponseEntity.ok("Cart cleared successfully.");
    }
}