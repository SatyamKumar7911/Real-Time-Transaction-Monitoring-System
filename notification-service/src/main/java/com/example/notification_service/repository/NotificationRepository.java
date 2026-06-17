package com.example.notification_service.repository;
import com.example.notification_service.entity.Notification;
import com.example.notification_service.entity.NotificationStatus;
import com.example.notification_service.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByEventId(String eventId);
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
    List<Notification> findByAccountNumberOrderByCreatedAtDesc(String accountNumber);
    List<Notification> findByStatus(NotificationStatus status);
    List<Notification> findByNotificationTypeAndStatus(NotificationType type, NotificationStatus status);
    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' AND n.retryCount < 3 AND n.createdAt > :cutoffTime")
    List<Notification> findFailedNotificationsForRetryOld(@Param("cutoffTime") LocalDateTime cutoffTime);
    List<Notification> findByTransactionId(String transactionId);
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.createdAt BETWEEN :startDate AND :endDate")
    Long countNotificationsByUserAndDateRange(@Param("userId") String userId, 
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    // Additional methods for NotificationService
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId, org.springframework.data.domain.Pageable pageable);

    Optional<Notification> findByIdAndUserId(Long id, String userId);

    long countByUserIdAndReadAtIsNull(String userId);

    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' AND n.nextRetryAt IS NOT NULL AND n.nextRetryAt <= :now")
    List<Notification> findFailedNotificationsForRetry(@Param("now") LocalDateTime now);

    @Query("SELECT n.channel, n.status, COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.createdAt BETWEEN :from AND :to GROUP BY n.channel, n.status")
    List<Object[]> getNotificationStatsByUser(@Param("userId") String userId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
    
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate")
    int deleteByCreatedAtBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
}