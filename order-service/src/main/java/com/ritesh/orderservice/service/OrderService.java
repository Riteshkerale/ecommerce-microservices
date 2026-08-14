package com.ritesh.orderservice.service;

import com.ritesh.orderservice.dtos.response.*;

import com.ritesh.orderservice.exception.ResourceNotFoundException;
import com.ritesh.orderservice.feign.CartClient;
import com.ritesh.orderservice.feign.ProductClient;
import com.ritesh.orderservice.feign.UserClient;
import com.ritesh.orderservice.repository.OrderRepository;

import com.ritesh.orderservice.entity.Order;
import com.ritesh.orderservice.entity.OrderItem;
import com.ritesh.orderservice.entity.OrderStatus;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
//    private final OrderItemRepository orderItemRepository;
private final CartClient cartClient;
    private final UserClient userClient;
    private final ProductClient productClient;

    public OrderResponse placeOrder(Long userId) {

        // 1. Find User

        UserResponse user;
        try {
            user = userClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        // 2. Get user's cart items
        CartResponse cart = cartClient.getCart(userId);
        List<CartItemResponse> cartItems = cart.getItems();

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 3. Create Order
        Order order = new Order();

        order.setUserId(userId);

        order.setOrderNumber(
                "ORD-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
                                .toUpperCase()
        );

        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 4. Convert CartItems -> OrderItems
        for (CartItemResponse cartItem : cartItems) {

            ProductResponse product =
                    productClient.getProductById(cartItem.getProductId());

            // Check stock
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for " + product.getName()
                );
            }

            // Tell Product Service to reduce stock
            productClient.reduceStock(
                    product.getId(),
                    cartItem.getQuantity()
            );

//            productRepository.save(product);

            BigDecimal subtotal = cartItem.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setSubtotal(subtotal);

            order.getOrderItems().add(orderItem);
        }

        // 5. Set total amount
        order.setTotalAmount(totalAmount);

        // 6. Save Order
        Order savedOrder = orderRepository.save(order);

//        // 7. Save Order Items
//        Order savedOrder = orderRepository.save(order);

        // 8. Clear Cart
        cartClient.clearCart(userId);

        return convertToResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        return convertToResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(Long userId) {

        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse convertToResponse(Order order) {

        List<OrderItemResponse> items = order.getOrderItems()
                .stream()
                .map(item ->
                        OrderItemResponse.builder()
                                .productId(item.getProductId())
//                                .productName(item.getProductN())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .subtotal(item.getSubtotal())
                                .build()
                )
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }
}