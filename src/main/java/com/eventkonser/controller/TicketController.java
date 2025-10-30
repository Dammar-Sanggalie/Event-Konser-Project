package com.eventkonser.controller;

import com.eventkonser.model.Ticket;
import com.eventkonser.service.TicketService;
import com.eventkonser.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TicketController {
    
    private final TicketService ticketService;
    
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
    @PostMapping
    public ResponseEntity<ApiResponse<Ticket>> createTicket(@RequestBody Ticket ticket) {
        Ticket createdTicket = ticketService.createTicket(ticket);
        return ResponseEntity.ok(ApiResponse.success("Tiket berhasil dibuat", createdTicket));
    }
    
    /**
     * PUT /api/tickets/{id} - Update ticket (Admin only)
     */
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
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok(ApiResponse.success("Tiket berhasil dihapus", null));
    }
}