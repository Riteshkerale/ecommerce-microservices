package com.ritesh.orderservice.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponse {

    private Long productId;

    private String productName;

    private String brand;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal totalPrice;

    private String thumbnailUrl;
}