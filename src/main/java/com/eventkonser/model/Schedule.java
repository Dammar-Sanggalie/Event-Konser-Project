package com.eventkonser.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "jadwal_event")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_jadwal")
    private Long idJadwal;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_event", nullable = false)
    @JsonBackReference
    private Event event;
    
    @Column(nullable = false)
    private LocalDate tanggal;
    
    @Column(name = "jam_mulai", nullable = false)
    private LocalTime jamMulai;
    
    @Column(name = "jam_selesai", nullable = false)
    private LocalTime jamSelesai;
    
    @Column(columnDefinition = "TEXT")
    private String keterangan;
}
