package com.example.accountservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "change_events")
@Data
public class ChangeEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "table_name", nullable = false)
    private String tableName;
    
    @Column(nullable = false)
    private String operation;
    
    @Column(name = "record_id", nullable = false)
    private Long recordId;
    
    @Column(name = "change_data", columnDefinition = "JSON", nullable = false)
    private String changeData;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private Boolean processed = false;
}
