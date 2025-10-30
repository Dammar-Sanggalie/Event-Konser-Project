package com.eventkonser.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "artis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Artist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_artis")
    private Long idArtis;
    
    @Column(name = "nama_artis", nullable = false, length = 100)
    private String namaArtis;
    
    @Column(length = 50)
    private String genre;
    
    @Column(name = "negara_asal", length = 50)
    private String negaraAsal;
    
    @Column(length = 100)
    private String kontak;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "foto_url")
    private String fotoUrl;
}