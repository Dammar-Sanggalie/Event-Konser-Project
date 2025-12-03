package com.eventkonser.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate tanggalMulai;
    
    @Column(name = "tanggal_selesai", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
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
    @JoinColumn(name = "id_kategori", nullable = true)
    private Category kategori;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_venue", nullable = true)
    private Venue venue;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Fetch(FetchMode.SUBSELECT)
    private List<Ticket> tickets = new ArrayList<>();
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Fetch(FetchMode.SUBSELECT)
    private List<Schedule> schedules = new ArrayList<>();
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Fetch(FetchMode.SUBSELECT)
    private List<Sponsor> sponsors = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "event_artis",
        joinColumns = @JoinColumn(name = "id_event"),
        inverseJoinColumns = @JoinColumn(name = "id_artis")
    )
    @Fetch(FetchMode.SUBSELECT)
    private List<Artist> artists = new ArrayList<>();
    
    @Transient
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Double startingPrice;
    
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
