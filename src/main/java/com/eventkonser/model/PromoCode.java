package com.eventkonser.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "promo_code")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromoCode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_promo")
    private Long idPromo;
    
    @Column(nullable = false, unique = true, length = 50)
    private String kode;
    
    @Column(columnDefinition = "TEXT")
    private String deskripsi;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "jenis_diskon", nullable = false)
    private DiscountType jenisDiskon;
    
    @Column(name = "nilai_diskon", nullable = false, precision = 10, scale = 2)
    private BigDecimal nilaiDiskon;
    
    @Column(name = "min_pembelian", precision = 10, scale = 2)
    private BigDecimal minPembelian;
    
    @Column(name = "max_penggunaan")
    private Integer maxPenggunaan;
    
    @Column(name = "total_digunakan")
    private Integer totalDigunakan = 0;
    
    @Column(name = "tanggal_mulai", nullable = false)
    private LocalDate tanggalMulai;
    
    @Column(name = "tanggal_selesai", nullable = false)
    private LocalDate tanggalSelesai;
    
    @Column(nullable = false)
    private Boolean active = true;
}