package com.eventkonser.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "wishlist", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_pengguna", "id_event"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wishlist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_wishlist")
    private Long idWishlist;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pengguna", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_event", nullable = false)
    private Event event;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "reminder_sent")
    private Boolean reminderSent = false;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.reminderSent = false;
    }
}
