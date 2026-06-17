package com.example.accountservice.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity 
@Table(name = "logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LogEntity {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private BankAccountEntity account;
    
    @Column(nullable = false)
    private String action;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "prev_balance", precision = 19, scale = 2)
    private BigDecimal prevBalance;
    
    @Column(name = "new_balance", precision = 19, scale = 2)
    private BigDecimal newBalance;
    
    private String reference;
    
    // New fields for transaction logging
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Column(name = "transaction_type")
    private String transactionType; // CREDIT, DEBIT, TRANSFER
    
    @Column(name = "from_account")
    private String fromAccount;
    
    @Column(name = "to_account")
    private String toAccount;
    
    @Column(name = "transaction_status")
    private String transactionStatus; // INITIATED, COMPLETED, FAILED
    
    @Column(name = "event_source")
    private String eventSource; // KAFKA, DIRECT
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(length = 500)
    private String description;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}