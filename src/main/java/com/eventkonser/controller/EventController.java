package com.eventkonser.controller;

import com.eventkonser.model.Event;
import com.eventkonser.model.EventStatus;
import com.eventkonser.service.EventService;
import com.eventkonser.service.TicketService;
import com.eventkonser.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventController {
    
    private final EventService eventService;
    private final TicketService ticketService;
    
    /**
     * GET /api/events - Get all events
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Event>>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(ApiResponse.success("Success", events));
    }
    
    /**
     * GET /api/events/upcoming - Get upcoming events
     */
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<Event>>> getUpcomingEvents() {
        List<Event> events = eventService.getUpcomingEvents();
        // Debug log the first event's startingPrice
        if (!events.isEmpty()) {
            log.debug("First event startingPrice: {}", events.get(0).getStartingPrice());
        }
        return ResponseEntity.ok(ApiResponse.success("Success", events));
    }
    
    /**
     * GET /api/events/popular - Get popular events
     */
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<Event>>> getPopularEvents() {
        List<Event> events = eventService.getPopularEvents();
        // Debug log the first event's startingPrice
        if (!events.isEmpty()) {
            log.debug("First popular event startingPrice: {}", events.get(0).getStartingPrice());
        }
        return ResponseEntity.ok(ApiResponse.success("Success", events));
    }
    
    /**
     * GET /api/events/{id} - Get event by ID with all details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Event>> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventWithDetails(id);
        return ResponseEntity.ok(ApiResponse.success("Success", event));
    }
    
    /**
     * GET /api/events/{id}/details - Get event by ID with all details (alias)
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<ApiResponse<Event>> getEventDetails(@PathVariable Long id) {
        Event event = eventService.getEventWithDetails(id);
        return ResponseEntity.ok(ApiResponse.success("Success", event));
    }
    
    /**
     * GET /api/events/{id}/starting-price - Get starting price for an event
     */
    @GetMapping("/{id}/starting-price")
    public ResponseEntity<ApiResponse<Double>> getStartingPrice(@PathVariable Long id) {
        Double startingPrice = ticketService.getStartingPriceByEvent(id);
        if (startingPrice == null) {
            return ResponseEntity.ok(ApiResponse.success("No tickets available", 0.0));
        }
        return ResponseEntity.ok(ApiResponse.success("Success", startingPrice));
    }
    
    /**
     * GET /api/events/search?q=keyword - Search events
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Event>>> searchEvents(
            @RequestParam(name = "q") String keyword) {
        List<Event> events = eventService.searchEvents(keyword);
        return ResponseEntity.ok(ApiResponse.success("Success", events));
    }
    
    /**
     * GET /api/events/category/{categoryId} - Filter by category
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<Event>>> getEventsByCategory(
            @PathVariable Long categoryId) {
        List<Event> events = eventService.getEventsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success("Success", events));
    }
    
    /**
     * GET /api/events/city/{city} - Filter by city
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<List<Event>>> getEventsByCity(
            @PathVariable String city) {
        List<Event> events = eventService.getEventsByCity(city);
        return ResponseEntity.ok(ApiResponse.success("Success", events));
    }
    
    /**
     * GET /api/events/artist/{artistId} - Filter by artist
     */
    @GetMapping("/artist/{artistId}")
    public ResponseEntity<ApiResponse<List<Event>>> getEventsByArtist(
            @PathVariable Long artistId) {
        List<Event> events = eventService.getEventsByArtist(artistId);
        return ResponseEntity.ok(ApiResponse.success("Success", events));
    }
    
    /**
     * GET /api/events/filter - Advanced filter
     * Query params: categoryId, city, startDate, endDate
     */
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<Event>>> filterEvents(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Event> events = eventService.filterEvents(categoryId, city, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Success", events));
    }
    
    /**
     * POST /api/events - Create new event (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<Event>> createEvent(@Valid @RequestBody Event event) {
        Event createdEvent = eventService.createEvent(event);
        return ResponseEntity.ok(ApiResponse.success("Event berhasil dibuat", createdEvent));
    }
    
    /**
     * PUT /api/events/{id} - Update event (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Event>> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody Event event) {
        Event updatedEvent = eventService.updateEvent(id, event);
        return ResponseEntity.ok(ApiResponse.success("Event berhasil diupdate", updatedEvent));
    }
    
    /**
     * PATCH /api/events/{id}/status - Update event status (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Event>> updateEventStatus(
            @PathVariable Long id,
            @RequestParam EventStatus status) {
        Event event = eventService.updateEventStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Status event berhasil diupdate", event));
    }
    
    /**
     * DELETE /api/events/{id} - Delete event (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok(ApiResponse.success("Event berhasil dihapus", null));
    }
}
