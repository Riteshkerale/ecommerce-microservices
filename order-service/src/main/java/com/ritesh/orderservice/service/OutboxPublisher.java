package com.ritesh.orderservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ritesh.orderservice.entity.OutboxEvent;
import com.ritesh.orderservice.event.OrderCreatedEvent;
import com.ritesh.orderservice.kafka.KafkaProducerService;
import com.ritesh.orderservice.repository.OutboxEventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(
            OutboxEventRepository outboxEventRepository,
            KafkaProducerService kafkaProducerService,
            ObjectMapper objectMapper) {

        this.outboxEventRepository = outboxEventRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {

        List<OutboxEvent> events =
                outboxEventRepository.findByStatus("PENDING");

        for (OutboxEvent event : events) {

            try {

                if ("OrderCreated".equals(event.getEventType())) {

                    OrderCreatedEvent orderCreatedEvent =
                            objectMapper.readValue(
                                    event.getPayload(),
                                    OrderCreatedEvent.class
                            );

                    kafkaProducerService.sendOrderCreatedEvent(
                            orderCreatedEvent
                    );

                    event.setStatus("SENT");

                    outboxEventRepository.save(event);

                    System.out.println(
                            "OUTBOX EVENT PUBLISHED TO KAFKA: "
                                    + event.getId()
                    );
                }

            } catch (Exception e) {

                System.out.println(
                        "FAILED TO PUBLISH OUTBOX EVENT: "
                                + event.getId()
                );

                e.printStackTrace();
            }
        }
    }
}