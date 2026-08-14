package com.ritesh.cartservice.repository;

import com.ritesh.cartservice.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Get all cart items of a user
    List<CartItem> findByUserId(Long userId);

    // Find a specific product in a user's cart
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    // Remove a specific product from user's cart
    void deleteByUserIdAndProductId(Long userId, Long productId);

}