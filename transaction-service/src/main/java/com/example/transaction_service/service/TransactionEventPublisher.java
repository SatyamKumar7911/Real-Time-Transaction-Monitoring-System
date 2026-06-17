package com.example.transaction_service.service;

import com.example.transaction_service.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionEventPublisher {
    
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
    
    private static final String TRANSACTION_TOPIC = "transaction-events";
    
    public void publishTransactionEvent(TransactionEvent event) {
        try {
            kafkaTemplate.send(TRANSACTION_TOPIC, event.getTransactionId(), event);
            log.info("Published transaction event: {} for transaction: {}", event.getEventType(), event.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to publish transaction event: {}", e.getMessage(), e);
        }
    }
}