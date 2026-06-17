package com.example.accountservice.service;

import com.example.accountservice.entity.ChangeEvent;
import com.example.accountservice.event.AccountEvent;
import com.example.accountservice.repository.ChangeEventRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CdcMonitorService {
    
    private final ChangeEventRepository changeEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String ACCOUNT_TOPIC = "account-events";
    
    @Scheduled(fixedDelay = 2000) // Poll every 2 seconds
    @Transactional
    public void monitorDatabaseChanges() {
        try {
            List<ChangeEvent> events = changeEventRepository.findUnprocessedAccountEvents();
            
            if (events.isEmpty()) {
                return;
            }
            
            log.info("🔍 CDC Monitor: Processing {} database change events", events.size());
            
            for (ChangeEvent event : events) {
                publishCdcEvent(event);
            }
            
            // Mark as processed
            List<Long> eventIds = events.stream()
                    .map(ChangeEvent::getId)
                    .collect(Collectors.toList());
            changeEventRepository.markAsProcessed(eventIds);
            
            log.info("✅ CDC Monitor: Successfully processed {} events", events.size());
            
        } catch (Exception e) {
            log.error("❌ CDC Monitor: Error processing database changes", e);
        }
    }
    
    private void publishCdcEvent(ChangeEvent event) {
        try {
            JsonNode data = objectMapper.readTree(event.getChangeData());
            
            if ("bank".equals(event.getTableName())) {
                publishAccountEvent(event, data);
            } else if ("users".equals(event.getTableName())) {
                publishUserEvent(event, data);
            }
            
        } catch (Exception e) {
            log.error("❌ CDC Monitor: Error publishing event for table {}", event.getTableName(), e);
        }
    }
    
    private void publishAccountEvent(ChangeEvent event, JsonNode data) {
        String operation = event.getOperation();
        String accountNumber = data.has("account_number") ? data.get("account_number").asText() : null;
        Long userId = data.has("user_id") ? data.get("user_id").asLong() : null;
        
        AccountEvent accountEvent = new AccountEvent();
        accountEvent.setAccountNumber(accountNumber);
        accountEvent.setUserId(userId);
        accountEvent.setTimestamp(LocalDateTime.now());
        
        switch (operation) {
            case "INSERT":
                accountEvent.setEventType("ACCOUNT_CREATED_VIA_CDC");
                break;
            case "UPDATE":
                BigDecimal balance = data.has("balance") ? 
                    new BigDecimal(data.get("balance").asText()) : null;
                accountEvent.setEventType("BALANCE_UPDATED_VIA_CDC");
                accountEvent.setBalance(balance);
                break;
            case "DELETE":
                accountEvent.setEventType("ACCOUNT_DELETED_VIA_CDC");
                break;
        }
        
        kafkaTemplate.send(ACCOUNT_TOPIC, accountNumber, accountEvent);
        log.info("📤 CDC: Published {} event for account {}", accountEvent.getEventType(), accountNumber);
    }
    
    private void publishUserEvent(ChangeEvent event, JsonNode data) {
        String operation = event.getOperation();
        String email = data.has("email") ? data.get("email").asText() : null;
        Long userId = data.has("id") ? data.get("id").asLong() : null;
        
        AccountEvent userEvent = new AccountEvent();
        userEvent.setUserId(userId);
        userEvent.setTimestamp(LocalDateTime.now());
        
        switch (operation) {
            case "INSERT":
                userEvent.setEventType("USER_CREATED_VIA_CDC");
                break;
            case "UPDATE":
                userEvent.setEventType("USER_UPDATED_VIA_CDC");
                break;
            case "DELETE":
                userEvent.setEventType("USER_DELETED_VIA_CDC");
                break;
        }
        
        kafkaTemplate.send(ACCOUNT_TOPIC, String.valueOf(userId), userEvent);
        log.info("📤 CDC: Published {} event for user {}", userEvent.getEventType(), email);
    }
}
