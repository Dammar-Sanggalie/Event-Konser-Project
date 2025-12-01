package com.eventkonser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO untuk Admin Payment Management
 * Include payment details + order + user info
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailResponse {
    
    private Long idPembayaran;
    private Long idPembelian;
    private String metodePembayaran;
    private BigDecimal jumlahBayar;
    private String statusPembayaran;
    private LocalDateTime tanggalBayar;
    private LocalDateTime createdAt;
    
    // Order info
    private Long orderId;
    private String eventName;
    private LocalDateTime tanggalPembelian;
    private String orderStatus;
    
    // User info
    private Long idPengguna;
    private String userNama;
    private String userEmail;
    private String userNoHp;
    
    // Summary
    private String paymentGatewayId;
    private LocalDateTime expiredAt;
}
