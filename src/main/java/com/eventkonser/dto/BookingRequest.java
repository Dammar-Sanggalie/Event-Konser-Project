package com.eventkonser.dto;

import lombok.Data;
import java.util.List;

@Data
public class BookingRequest {
    // Frontend sends this structure:
    // { idPengguna, totalHarga, items: [{idTiket, jumlah}, ...], discountAmount, promoCode, subtotal }
    
    private Long idPengguna;        // User ID from JWT
    private Double totalHarga;      // Total price (for audit trail)
    private List<BookingItem> items; // Array of tickets booked
    private Double discountAmount;  // Discount amount applied from promo
    private String promoCode;       // Promo code that was applied
    private Double subtotal;        // Subtotal before discount
    
    // Legacy support (for backward compatibility)
    private Long userId;
    private Long ticketId;
    private Integer quantity;
    
    @Data
    public static class BookingItem {
        private Long idTiket;       // Ticket ID
        private Integer jumlah;     // Quantity
    }
}

