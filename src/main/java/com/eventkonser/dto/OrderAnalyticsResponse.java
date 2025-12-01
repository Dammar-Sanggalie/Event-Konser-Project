package com.eventkonser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for order analytics and management
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAnalyticsResponse {
    private Long idPembelian;
    
    // User information
    private Long idPengguna;
    private String userName;
    private String userEmail;
    private String userPhone;
    
    // Order details
    private Integer jumlah;
    private BigDecimal totalHarga;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private String status; // PENDING, PAID, USED, CANCELLED, EXPIRED, REFUNDED, COMPLETED
    private LocalDateTime tanggalPembelian;
    
    // Event and ticket info
    private String eventName;
    private String ticketType;
    private Long ticketId;
    
    // Payment info (denormalized)
    private String statusPembayaran; // PENDING, SUCCESS, FAILED, EXPIRED, REFUNDED
    private LocalDateTime tanggalBayar;
    
    // QR Code for check-in
    private String qrCode;
    
    // Additional metadata
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
