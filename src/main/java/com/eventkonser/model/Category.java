package com.eventkonser.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "kategori_event")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kategori")
    private Long idKategori;
    
    @Column(name = "nama_kategori", nullable = false, length = 50)
    private String namaKategori;
    
    @Column(columnDefinition = "TEXT")
    private String deskripsi;
}
