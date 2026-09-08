package com.ritesh.notification_service.dto;

public record OrderCreatedEvent(
        Long orderId,
        Long userId,
        String email,
        String firstName
) {
}