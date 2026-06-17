package com.example.accountservice.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
@Entity @Table(name = "bank")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BankAccountEntity {
@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
@ManyToOne(optional = false, fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private UserEntity user;
@Column(name = "account_number", nullable = false, unique = true)
private String accountNumber;
@Column(name = "account_type", nullable = false)
private String accountType;
@Column(nullable = false, length = 3)
private String currency;
@Column(nullable = false, precision = 19, scale = 2)
private BigDecimal balance;
@Column(nullable = false)
private String status;
@Version
private Long version;
}