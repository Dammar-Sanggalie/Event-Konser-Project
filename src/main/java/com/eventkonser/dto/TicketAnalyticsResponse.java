package com.eventkonser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Simplified DTO for analytics page - no circular references
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketAnalyticsResponse {
    private Long idTiket;
    private String jenisTiket;
    private BigDecimal harga;
    private Integer stok;
    private Integer stokAwal;
    private String deskripsi;
    
    // Event info (denormalized)
    private Long idEvent;
    private String namaEvent;
    private String status; // TERSEDIA, HABIS, DITUTUP
}
