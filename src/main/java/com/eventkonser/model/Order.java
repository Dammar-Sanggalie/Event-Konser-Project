package com.eventkonser.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pembelian_tiket")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pembelian")
    private Long idPembelian;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pengguna")
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tiket", nullable = false)
    private Ticket ticket;
    
    @Column(nullable = false)
    private Integer jumlah;
    
    @Column(name = "total_harga", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalHarga;
    
    @Column(name = "tanggal_pembelian", nullable = false)
    private LocalDateTime tanggalPembelian = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;
    
    @Column(name = "qr_code", unique = true)
    private String qrCode;
    
    @Column(name = "used_at")
    private LocalDateTime usedAt;
    
    @Column(name = "expired_at")
    private LocalDateTime expiredAt;
    
    @Column(name = "event_name")
    private String eventName;
    
    @Column(name = "event_date")
    private String eventDate;
    
    @Column(name = "venue_name")
    private String venueName;
    
    @Column(name = "ticket_type")
    private String ticketType;
    
    @Column(name = "event_image_url")
    private String eventImageUrl;
    
    @Column(name = "id_event")
    private Long idEvent;
    
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Column(name = "promo_code")
    private String promoCode;
    
    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Payment payment;
    
    @PrePersist
    public void prePersist() {
        if (this.status == OrderStatus.PENDING) {
            this.expiredAt = LocalDateTime.now().plusMinutes(15);
        }
        this.qrCode = generateQRCode();
        if (this.tanggalPembelian == null) {
            this.tanggalPembelian = LocalDateTime.now();
        }
    }
    
    private String generateQRCode() {
        return "TKT-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }
}