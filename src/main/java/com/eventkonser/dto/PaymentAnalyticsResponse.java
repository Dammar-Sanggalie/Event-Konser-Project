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
public class PaymentAnalyticsResponse {
    private Long idPembayaran;
    private Long idPembelian;
    private BigDecimal jumlahBayar;
    private String metodePembayaran;
    private String statusPembayaran; // PENDING, SUCCESS, FAILED, EXPIRED, REFUNDED
    private LocalDateTime tanggalBayar;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private String paymentUrl;
    private String paymentGatewayId;
    private String notes;
}
