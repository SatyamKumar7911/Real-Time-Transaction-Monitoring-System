package com.example.transaction_service.service;

import com.example.transaction_service.entity.ChangeEvent;
import com.example.transaction_service.event.TransactionEvent;
import com.example.transaction_service.repository.ChangeEventRepository;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CdcMonitorService {
    
    private final ChangeEventRepository changeEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String TRANSACTION_TOPIC = "transaction-events";
    
    @Scheduled(fixedDelay = 2000) // Poll every 2 seconds
    @Transactional
    public void monitorDatabaseChanges() {
        try {
            List<ChangeEvent> events = changeEventRepository.findUnprocessedTransactionEvents();
            
            if (events.isEmpty()) {
                return;
            }
            
            log.info("🔍 CDC Monitor: Processing {} transaction log events", events.size());
            
            for (ChangeEvent event : events) {
                publishTransactionEvent(event);
            }
            
            // Mark as processed
            List<Long> eventIds = events.stream()
                    .map(ChangeEvent::getId)
                    .collect(Collectors.toList());
            changeEventRepository.markAsProcessed(eventIds);
            
            log.info("✅ CDC Monitor: Successfully processed {} transaction events", events.size());
            
        } catch (Exception e) {
            log.error("❌ CDC Monitor: Error processing transaction changes", e);
        }
    }
    
    private void publishTransactionEvent(ChangeEvent event) {
        try {
            JsonNode data = objectMapper.readTree(event.getChangeData());
            
            Long accountId = data.has("account_id") ? data.get("account_id").asLong() : null;
            String action = data.has("action") ? data.get("action").asText() : "UNKNOWN";
            BigDecimal amount = data.has("amount") ? new BigDecimal(data.get("amount").asText()) : null;
            String reference = data.has("reference") ? data.get("reference").asText() : null;
            
            TransactionEvent txEvent = new TransactionEvent();
            txEvent.setTransactionId(reference != null ? reference : UUID.randomUUID().toString());
            txEvent.setEventType("TRANSACTION_LOGGED_VIA_CDC");
            txEvent.setDescription("Transaction " + action + " logged directly in database");
            txEvent.setAmount(amount);
            txEvent.setTimestamp(LocalDateTime.now());
            
            kafkaTemplate.send(TRANSACTION_TOPIC, txEvent.getTransactionId(), txEvent);
            log.info("📤 CDC: Published transaction event for action {} ({})", action, txEvent.getTransactionId());
            
        } catch (Exception e) {
            log.error("❌ CDC Monitor: Error publishing transaction event", e);
        }
    }
}
