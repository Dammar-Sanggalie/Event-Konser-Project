package com.eventkonser.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("id")  // Alias untuk JSON serialization
    private Long idNotifikasi;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pengguna")
    @JsonIgnore  // Prevent infinite recursion
    private User user;
    
    @Column(name = "judul", nullable = false)
    @JsonProperty("title")  // Alias untuk JSON
    private String judul;
    
    @Column(name = "pesan", columnDefinition = "TEXT")
    @JsonProperty("message")  // Alias untuk JSON
    private String pesan;
    
    @Column(name = "tipe", nullable = false)
    @JsonProperty("type")  // Alias untuk JSON
    private String tipe;  // "order", "payment", "event", "promo"
    
    @Column(name = "icon")
    @JsonProperty("icon")
    private String icon;  // Icon untuk UI
    
    @Column(name = "link")
    @JsonProperty("link")
    private String link;  // Link ke halaman terkait
    
    @Column(name = "is_read", nullable = false)
    @JsonProperty("isRead")
    private Boolean isRead = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @JsonProperty("updatedAt")
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
