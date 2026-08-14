package com.ritesh.cartservice.service;

import com.ritesh.cartservice.dtos.request.AddToCartRequest;
import com.ritesh.cartservice.dtos.response.CartItemResponse;
import com.ritesh.cartservice.dtos.response.CartResponse;
import com.ritesh.cartservice.dtos.response.ProductResponse;
import com.ritesh.cartservice.dtos.response.UserResponse;
import com.ritesh.cartservice.entity.CartItem;
import com.ritesh.cartservice.exception.ProductNotFoundException;
import com.ritesh.cartservice.exception.UserNotFoundException;
import com.ritesh.cartservice.feign.ProductClient;
import com.ritesh.cartservice.feign.UserClient;
import com.ritesh.cartservice.repository.CartItemRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserClient userClient;
    private final ProductClient productClient;

    // ADD PRODUCT TO CART
    public CartResponse addToCart(Long userId, AddToCartRequest request) {

        // Check whether user exists
        UserResponse user;

        try {
            user = userClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException(
                    "User not found with id : " + userId
            );
        }

        // Check whether product exists
        ProductResponse product;

        try {
            product = productClient.getProductById(request.getProductId());
        } catch (FeignException.NotFound e) {
            throw new ProductNotFoundException(
                    "Product not found with id : " + request.getProductId()
            );
        }

        // Check whether product already exists in cart
        Optional<CartItem> existingCartItem =
                cartItemRepository.findByUserIdAndProductId(
                        userId,
                        request.getProductId()
                );

        CartItem cartItem;

        if (existingCartItem.isPresent()) {

            cartItem = existingCartItem.get();

            cartItem.setQuantity(
                    cartItem.getQuantity() + request.getQuantity()
            );

        } else {

            cartItem = new CartItem();

            cartItem.setUserId(userId);
            cartItem.setProductId(request.getProductId());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(product.getPrice());
        }

        cartItemRepository.save(cartItem);

        return getCart(userId);
    }


    // GET USER CART
    public CartResponse getCart(Long userId) {

        // Check whether user exists
        try {
            userClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException(
                    "User not found with id : " + userId
            );
        }

        List<CartItem> cartItems =
                cartItemRepository.findByUserId(userId);

        List<CartItemResponse> itemResponses =
                cartItems.stream()
                        .map(this::mapToCartItemResponse)
                        .toList();

        BigDecimal grandTotal =
                itemResponses.stream()
                        .map(CartItemResponse::getTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems =
                cartItems.stream()
                        .mapToInt(CartItem::getQuantity)
                        .sum();

        CartResponse response = new CartResponse();

        response.setItems(itemResponses);
        response.setGrandTotal(grandTotal);
        response.setTotalItems(totalItems);

        return response;
    }


    // REMOVE PRODUCT FROM CART
    @Transactional
    public void removeProductFromCart(Long userId, Long productId) {

        // Check whether user exists
        try {
            userClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException(
                    "User not found with id : " + userId
            );
        }

        cartItemRepository.deleteByUserIdAndProductId(
                userId,
                productId
        );
    }


    // CLEAR CART
    public void clearCart(Long userId) {

        // Check whether user exists
        try {
            userClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException(
                    "User not found with id : " + userId
            );
        }

        List<CartItem> cartItems =
                cartItemRepository.findByUserId(userId);

        cartItemRepository.deleteAll(cartItems);
    }


    // ENTITY -> DTO
    private CartItemResponse mapToCartItemResponse(CartItem cartItem) {

        ProductResponse product =
                productClient.getProductById(
                        cartItem.getProductId()
                );

        CartItemResponse response = new CartItemResponse();

        response.setProductId(cartItem.getProductId());

        response.setProductName(product.getName());
        response.setBrand(product.getBrand());
        response.setThumbnailUrl(product.getThumbnailUrl());

        response.setQuantity(cartItem.getQuantity());
        response.setPrice(cartItem.getPrice());

        response.setTotalPrice(
                cartItem.getPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        cartItem.getQuantity()
                                )
                        )
        );

        return response;
    }
}