package com.ritesh.cartservice.dtos.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponse {

    private List<CartItemResponse> items;

    private BigDecimal grandTotal;

    private Integer totalItems;

}