package com.eventkonser.repository;

import com.eventkonser.model.Notification;
import com.eventkonser.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository untuk Notification
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find all notifications for a user, ordered by created date (newest first)
     */
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Find unread notifications for a user
     */
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
    
    /**
     * Find notifications by type and user
     */
    List<Notification> findByUserAndTipeOrderByCreatedAtDesc(User user, String tipe);
    
    /**
     * Count unread notifications for a user
     */
    Long countByUserAndIsReadFalse(User user);
}


