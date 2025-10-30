package com.eventkonser.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "venue")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venue")
    private Long idVenue;
    
    @Column(name = "nama_venue", nullable = false, length = 100)
    private String namaVenue;
    
    @Column(columnDefinition = "TEXT")
    private String alamat;
    
    @Column(nullable = false)
    private Integer kapasitas;
    
    @Column(length = 50)
    private String kota;
}