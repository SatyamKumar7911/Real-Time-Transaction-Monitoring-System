package com.example.transaction_service.repository;

import com.example.transaction_service.entity.ChangeEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangeEventRepository extends JpaRepository<ChangeEvent, Long> {
    
    @Query("SELECT ce FROM ChangeEvent ce WHERE ce.processed = false " +
           "AND ce.tableName = 'logs' " +
           "ORDER BY ce.id ASC")
    List<ChangeEvent> findUnprocessedTransactionEvents();
    
    @Modifying
    @Query("UPDATE ChangeEvent ce SET ce.processed = true WHERE ce.id IN :ids")
    void markAsProcessed(@Param("ids") List<Long> ids);
}
