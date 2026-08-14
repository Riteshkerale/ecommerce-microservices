package com.ritesh.orderservice.dtos.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponse {

    private Long id;

    private Long userId;

    private List<CartItemResponse> items;

    private BigDecimal totalPrice;
}