package com.example.accountservice.service;

import com.example.accountservice.event.AccountEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountEventPublisher {
    
    private final KafkaTemplate<String, AccountEvent> kafkaTemplate;
    
    private static final String ACCOUNT_TOPIC = "account-events";
    
    public void publishAccountEvent(AccountEvent event) {
        try {
            kafkaTemplate.send(ACCOUNT_TOPIC, event.getAccountNumber(), event);
            log.info("Published account event: {} for account: {}", event.getEventType(), event.getAccountNumber());
        } catch (Exception e) {
            log.error("Failed to publish account event: {}", e.getMessage(), e);
        }
    }
}