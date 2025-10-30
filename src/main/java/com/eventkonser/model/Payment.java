package com.eventkonser.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pembayaran")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pembayaran")
    private Long idPembayaran;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pembelian", nullable = false, unique = true)
    @JsonBackReference
    private Order order;
    
    @Column(name = "metode_pembayaran", nullable = false, length = 50)
    private String metodePembayaran;
    
    @Column(name = "tanggal_bayar")
    private LocalDateTime tanggalBayar;
    
    @Column(name = "jumlah_bayar", nullable = false, precision = 10, scale = 2)
    private BigDecimal jumlahBayar;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status_pembayaran", nullable = false, length = 20)
    private PaymentStatus statusPembayaran = PaymentStatus.PENDING;
    
    @Column(name = "payment_gateway_id")
    private String paymentGatewayId;
    
    @Column(name = "payment_url")
    private String paymentUrl;
    
    @Column(name = "expired_at")
    private LocalDateTime expiredAt;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}