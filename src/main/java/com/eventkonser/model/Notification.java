package com.eventkonser.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Notification Entity - Untuk menyimpan notifikasi user
 */
@Entity
@Table(name = "notifikasi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notifikasi")
    private Long idNotifikasi;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pengguna")
    @JsonIgnore  // Prevent infinite recursion
    private User user;
    
    @Column(name = "judul", nullable = false)
    private String judul;
    
    @Column(name = "pesan", columnDefinition = "TEXT")
    private String pesan;
    
    @Column(name = "tipe", nullable = false)
    private String tipe;  // "order", "payment", "event", "promo"
    
    @Column(name = "icon")
    private String icon;  // Icon untuk UI
    
    @Column(name = "link")
    private String link;  // Link ke halaman terkait
    
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
