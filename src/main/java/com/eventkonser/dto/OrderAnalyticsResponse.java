package com.eventkonser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Simplified DTO for analytics page - no circular references
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAnalyticsResponse {
    private Long idPembelian;
    private Long idPengguna; // from user.idPengguna
    private Integer jumlah;
    private BigDecimal totalHarga;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private String status; // PENDING, PAID, USED, CANCELLED, EXPIRED, REFUNDED
    private LocalDateTime tanggalPembelian;
    private String eventName;
    private String ticketType;
    
    // Payment info (denormalized)
    private String statusPembayaran; // PENDING, SUCCESS, FAILED, EXPIRED, REFUNDED
    private LocalDateTime tanggalBayar;
}
