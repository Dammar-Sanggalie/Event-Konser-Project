package com.eventkonser.controller;

import com.eventkonser.config.JwtTokenProvider;
import com.eventkonser.dto.ApiResponse;
import com.eventkonser.model.Notification;
import com.eventkonser.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Notification Controller
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {
    
    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;
    
    /**
     * GET /api/notifications - Get all notifications for current user
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getNotifications(
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.success("Success", List.of()));
        }
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success("Success", notifications));
    }
    
    /**
     * GET /api/notifications/unread - Get unread notifications
     */
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<Notification>>> getUnreadNotifications(
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.success("Success", List.of()));
        }
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success("Success", notifications));
    }
    
    /**
     * GET /api/notifications/count-unread - Count unread notifications
     */
    @GetMapping("/count-unread")
    public ResponseEntity<ApiResponse<Long>> countUnreadNotifications(
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.success("Success", 0L));
        }
        Long count = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success("Success", count));
    }
    
    /**
     * GET /api/notifications/type/{type} - Get notifications by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<Notification>>> getNotificationsByType(
            @PathVariable String type,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.success("Success", List.of()));
        }
        List<Notification> notifications = notificationService.getNotificationsByType(userId, type);
        return ResponseEntity.ok(ApiResponse.success("Success", notifications));
    }
    
    /**
     * POST /api/notifications/{id}/read - Mark notification as read
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Notification>> markAsRead(@PathVariable Long id) {
        Notification notification = notificationService.markAsRead(id);
        if (notification == null) {
            return ResponseEntity.ok(ApiResponse.error("Notification not found"));
        }
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", notification));
    }
    
    /**
     * POST /api/notifications/read-all - Mark all as read
     */
    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.error("Unauthorized"));
        }
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }
    
    /**
     * DELETE /api/notifications/{id} - Delete notification
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(ApiResponse.success("Notification deleted", null));
    }
    
    /**
     * DELETE /api/notifications - Delete all notifications
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteAllNotifications(
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.error("Unauthorized"));
        }
        notificationService.deleteAllNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success("All notifications deleted", null));
    }
    
    /**
     * Helper method to extract userId from JWT token
     */
    private Long extractUserIdFromToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        try {
            return jwtTokenProvider.getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
}
