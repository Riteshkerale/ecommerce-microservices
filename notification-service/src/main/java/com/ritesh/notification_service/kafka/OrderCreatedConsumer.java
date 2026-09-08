package com.ritesh.notification_service.kafka;

import com.ritesh.notification_service.dto.OrderCreatedEvent;
import com.ritesh.notification_service.entity.ProcessedEvent;
import com.ritesh.notification_service.repository.ProcessedEventRepository;
import com.ritesh.notification_service.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderCreatedConsumer {

    private final EmailService emailService;
    private final ProcessedEventRepository processedEventRepository;

    public OrderCreatedConsumer(
            EmailService emailService,
            ProcessedEventRepository processedEventRepository) {

        this.emailService = emailService;
        this.processedEventRepository = processedEventRepository;
    }

    @KafkaListener(
            topics = "order-created",
            groupId = "notification-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {

        String eventId = event.orderId().toString();

        // Check if event was already processed
        if (processedEventRepository.existsByEventId(eventId)) {

            System.out.println(
                    "DUPLICATE EVENT IGNORED: " + eventId
            );

            return;
        }

        System.out.println(
                "ORDER CREATED EVENT RECEIVED: " + event
        );

        // Process the event
        emailService.sendOrderConfirmationEmail(
                event.userId(),
                event.orderId(),
                event.email(),
                event.firstName()
        );

        // Mark event as processed
        ProcessedEvent processedEvent = ProcessedEvent.builder()
                .eventId(eventId)
                .processedAt(LocalDateTime.now())
                .build();

        processedEventRepository.save(processedEvent);

        System.out.println(
                "EVENT MARKED AS PROCESSED: " + eventId
        );
    }
}