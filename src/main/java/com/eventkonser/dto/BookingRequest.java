package com.eventkonser.dto;

import lombok.Data;
import java.util.List;

@Data
public class BookingRequest {
    // Frontend sends this structure:
    // { idPengguna, totalHarga, items: [{idTiket, jumlah}, ...] }
    
    private Long idPengguna;        // User ID from JWT
    private Double totalHarga;      // Total price (for audit trail)
    private List<BookingItem> items; // Array of tickets booked
    
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

