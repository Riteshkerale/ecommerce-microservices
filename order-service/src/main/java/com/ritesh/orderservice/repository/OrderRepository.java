package com.ritesh.orderservice.repository;

import com.ritesh.orderservice.entity.Order;
import com.ritesh.orderservice.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Get all orders of a user
    List<Order> findByUserId(Long userId);

    // Get orders by status
    List<Order> findByStatus(OrderStatus status);

    // Find order using order number
    Optional<Order> findByOrderNumber(String orderNumber);

    // Check if order number already exists
    boolean existsByOrderNumber(String orderNumber);

    // Get user's orders sorted by latest first
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
}