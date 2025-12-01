package com.eventkonser.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "tiket")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tiket")
    private Long idTiket;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_event", nullable = false)
    @JsonBackReference
    private Event event;
    
    @Column(name = "jenis_tiket", nullable = false, length = 50)
    private String jenisTiket;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal harga;
    
    @Column(nullable = false)
    private Integer stok;
    
    @Column(name = "stok_awal")
    private Integer stokAwal;
    
    @Column(columnDefinition = "TEXT")
    private String deskripsi;
    
    @Column(name = "max_pembelian")
    private Integer maxPembelian = 5;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TicketStatus status = TicketStatus.AVAILABLE;
}
