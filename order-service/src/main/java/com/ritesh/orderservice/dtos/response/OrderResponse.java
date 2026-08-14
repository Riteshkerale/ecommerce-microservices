package com.ritesh.orderservice.dtos.response;

import com.ritesh.orderservice.entity.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long orderId;

    private String orderNumber;

    private OrderStatus status;

    private BigDecimal totalAmount;

    private LocalDateTime createdAt;

    private List<OrderItemResponse> items;
}