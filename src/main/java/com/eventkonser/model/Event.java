package com.eventkonser.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "event")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_event")
    private Long idEvent;
    
    @Column(name = "nama_event", nullable = false, length = 200)
    private String namaEvent;
    
    @Column(columnDefinition = "TEXT")
    private String deskripsi;
    
    @Column(name = "tanggal_mulai", nullable = false)
    private LocalDate tanggalMulai;
    
    @Column(name = "tanggal_selesai", nullable = false)
    private LocalDate tanggalSelesai;
    
    @Column(length = 100)
    private String penyelenggara;
    
    @Column(name = "banner_url")
    private String bannerUrl;
    
    @Column(name = "poster_url")
    private String posterUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EventStatus status = EventStatus.UPCOMING;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_kategori", nullable = false)
    private Category kategori;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_venue", nullable = false)
    private Venue venue;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Ticket> tickets = new ArrayList<>();
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Schedule> schedules = new ArrayList<>();
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Sponsor> sponsors = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "event_artis",
        joinColumns = @JoinColumn(name = "id_event"),
        inverseJoinColumns = @JoinColumn(name = "id_artis")
    )
    private List<Artist> artists = new ArrayList<>();
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
