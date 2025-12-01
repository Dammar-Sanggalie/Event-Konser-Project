package com.eventkonser.controller;

import com.eventkonser.model.Ticket;
import com.eventkonser.service.TicketService;
import com.eventkonser.dto.ApiResponse;
import com.eventkonser.dto.TicketAnalyticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TicketController {
    
    private final TicketService ticketService;
    
    /**
     * GET /api/tickets - Get all tickets (Admin)
     * Return TicketAnalyticsResponse for analytics dashboard (no circular refs)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TicketAnalyticsResponse>>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        List<TicketAnalyticsResponse> analyticsData = tickets.stream()
            .map(ticket -> TicketAnalyticsResponse.builder()
                .idTiket(ticket.getIdTiket())
                .jenisTiket(ticket.getJenisTiket())
                .harga(ticket.getHarga())
                .stok(ticket.getStok())
                .stokAwal(ticket.getStokAwal())
                .deskripsi(ticket.getDeskripsi())
                .idEvent(ticket.getEvent() != null ? ticket.getEvent().getIdEvent() : null)
                .namaEvent(ticket.getEvent() != null ? ticket.getEvent().getNamaEvent() : null)
                .status(ticket.getStatus() != null ? ticket.getStatus().toString() : null)
                .build())
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Success", analyticsData));
    }
    
    /**
     * GET /api/tickets/event/{eventId} - Get tickets by event
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<List<Ticket>>> getTicketsByEvent(
            @PathVariable Long eventId) {
        List<Ticket> tickets = ticketService.getTicketsByEvent(eventId);
        return ResponseEntity.ok(ApiResponse.success("Success", tickets));
    }
    
    /**
     * GET /api/tickets/event/{eventId}/available - Get available tickets
     */
    @GetMapping("/event/{eventId}/available")
    public ResponseEntity<ApiResponse<List<Ticket>>> getAvailableTickets(
            @PathVariable Long eventId) {
        List<Ticket> tickets = ticketService.getAvailableTicketsByEvent(eventId);
        return ResponseEntity.ok(ApiResponse.success("Success", tickets));
    }
    
    /**
     * GET /api/tickets/{id} - Get ticket by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Ticket>> getTicketById(@PathVariable Long id) {
        Ticket ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ApiResponse.success("Success", ticket));
    }
    
    /**
     * POST /api/tickets - Create new ticket (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<Ticket>> createTicket(@RequestBody Ticket ticket) {
        Ticket createdTicket = ticketService.createTicket(ticket);
        return ResponseEntity.ok(ApiResponse.success("Tiket berhasil dibuat", createdTicket));
    }
    
    /**
     * PUT /api/tickets/{id} - Update ticket (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Ticket>> updateTicket(
            @PathVariable Long id,
            @RequestBody Ticket ticket) {
        Ticket updatedTicket = ticketService.updateTicket(id, ticket);
        return ResponseEntity.ok(ApiResponse.success("Tiket berhasil diupdate", updatedTicket));
    }
    
    /**
     * DELETE /api/tickets/{id} - Delete ticket (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok(ApiResponse.success("Tiket berhasil dihapus", null));
    }
}