package com.eventkonser.service;

import com.eventkonser.model.Notification;
import com.eventkonser.model.User;
import com.eventkonser.repository.NotificationRepository;
import com.eventkonser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Service untuk Notification
 * Handle logic create, read, update, delete notifikasi
 */
@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    
    /**
     * Send/Create notification untuk user
     */
    public Notification sendNotification(Long userId, String judul, String pesan, String tipe, String link) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return null;
        }
        
        User user = userOpt.get();
        
        Notification notification = Notification.builder()
                .user(user)
                .judul(judul)
                .pesan(pesan)
                .tipe(tipe)
                .link(link)
                .icon(getIconByType(tipe))
                .isRead(false)
                .build();
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Get all notifications for user
     */
    public List<Notification> getUserNotifications(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return List.of();
        }
        return notificationRepository.findByUserOrderByCreatedAtDesc(userOpt.get());
    }
    
    /**
     * Get unread notifications for user
     */
    public List<Notification> getUnreadNotifications(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return List.of();
        }
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(userOpt.get());
    }
    
    /**
     * Get notifications by type
     */
    public List<Notification> getNotificationsByType(Long userId, String tipe) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return List.of();
        }
        return notificationRepository.findByUserAndTipeOrderByCreatedAtDesc(userOpt.get(), tipe);
    }
    
    /**
     * Count unread notifications
     */
    public Long countUnreadNotifications(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return 0L;
        }
        return notificationRepository.countByUserAndIsReadFalse(userOpt.get());
    }
    
    /**
     * Mark notification as read
     */
    public Notification markAsRead(Long notificationId) {
        Optional<Notification> notifOpt = notificationRepository.findById(notificationId);
        if (notifOpt.isEmpty()) {
            return null;
        }
        
        Notification notification = notifOpt.get();
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }
    
    /**
     * Mark all notifications as read for user
     */
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = getUnreadNotifications(userId);
        unreadNotifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }
    
    /**
     * Delete notification
     */
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
    
    /**
     * Delete all notifications for user
     */
    public void deleteAllNotifications(Long userId) {
        List<Notification> notifications = getUserNotifications(userId);
        notificationRepository.deleteAll(notifications);
    }
    
    /**
     * Get icon by notification type
     */
    private String getIconByType(String tipe) {
        return switch (tipe) {
            case "order" -> "📦";
            case "payment" -> "💳";
            case "event" -> "🎤";
            case "promo" -> "🎁";
            default -> "📬";
        };
    }
}
