package com.eventkonser.controller;

import com.eventkonser.model.Event;
import com.eventkonser.model.EventStatus;
import com.eventkonser.model.Category;
import com.eventkonser.model.Venue;
import com.eventkonser.service.EventService;
import com.eventkonser.service.TicketService;
import com.eventkonser.service.CategoryService;
import com.eventkonser.service.VenueService;
import com.eventkonser.dto.ApiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventController {
    
    private final EventService eventService;
    private final TicketService ticketService;
    private final CategoryService categoryService;
    private final VenueService venueService;
    
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
    public ResponseEntity<ApiResponse<Event>> createEvent(@RequestBody JsonNode eventNode) {
        
        log.debug("Create event request body: {}", eventNode.toString());
        
        // Map the JSON node to Event entity
        Event event = new Event();
        
        if (eventNode.has("namaEvent")) {
            event.setNamaEvent(eventNode.get("namaEvent").asText());
        }
        if (eventNode.has("deskripsi")) {
            event.setDeskripsi(eventNode.get("deskripsi").asText());
        }
        if (eventNode.has("penyelenggara")) {
            event.setPenyelenggara(eventNode.get("penyelenggara").asText());
        }
        if (eventNode.has("posterUrl")) {
            event.setPosterUrl(eventNode.get("posterUrl").asText(null));
        }
        if (eventNode.has("bannerUrl")) {
            event.setBannerUrl(eventNode.get("bannerUrl").asText(null));
        }
        if (eventNode.has("status")) {
            try {
                event.setStatus(EventStatus.valueOf(eventNode.get("status").asText()));
            } catch (IllegalArgumentException e) {
                event.setStatus(EventStatus.UPCOMING);
            }
        }
        if (eventNode.has("tanggalMulai")) {
            String tanggalMulaiStr = eventNode.get("tanggalMulai").asText(null);
            if (tanggalMulaiStr != null) {
                LocalDate tanggalMulai = parseISODateTimeToLocalDate(tanggalMulaiStr);
                event.setTanggalMulai(tanggalMulai);
            }
        }
        if (eventNode.has("tanggalSelesai")) {
            String tanggalSelesaiStr = eventNode.get("tanggalSelesai").asText(null);
            if (tanggalSelesaiStr != null) {
                LocalDate tanggalSelesai = parseISODateTimeToLocalDate(tanggalSelesaiStr);
                event.setTanggalSelesai(tanggalSelesai);
            }
        }
        
        // Handle kategori ID
        if (eventNode.has("idKategori") && !eventNode.get("idKategori").isNull()) {
            Long kategoriId = eventNode.get("idKategori").asLong();
            log.info("Setting kategori with ID: {}", kategoriId);
            Category kategori = categoryService.getCategoryById(kategoriId);
            event.setKategori(kategori);
        }
        
        // Handle venue ID
        if (eventNode.has("idVenue") && !eventNode.get("idVenue").isNull()) {
            Long venueId = eventNode.get("idVenue").asLong();
            log.info("Setting venue with ID: {}", venueId);
            Venue venue = venueService.getVenueById(venueId);
            event.setVenue(venue);
        }
        
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
            @RequestBody JsonNode eventNode) {
        
        log.debug("Update event request body: {}", eventNode.toString());
        
        // Map the JSON node to Event entity
        Event event = new Event();
        
        if (eventNode.has("namaEvent")) {
            event.setNamaEvent(eventNode.get("namaEvent").asText());
        }
        if (eventNode.has("deskripsi")) {
            event.setDeskripsi(eventNode.get("deskripsi").asText());
        }
        if (eventNode.has("penyelenggara")) {
            event.setPenyelenggara(eventNode.get("penyelenggara").asText());
        }
        if (eventNode.has("posterUrl")) {
            event.setPosterUrl(eventNode.get("posterUrl").asText(null));
        }
        if (eventNode.has("bannerUrl")) {
            event.setBannerUrl(eventNode.get("bannerUrl").asText(null));
        }
        if (eventNode.has("status")) {
            try {
                event.setStatus(EventStatus.valueOf(eventNode.get("status").asText()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status: {}", eventNode.get("status").asText());
            }
        }
        if (eventNode.has("tanggalMulai")) {
            String tanggalMulaiStr = eventNode.get("tanggalMulai").asText(null);
            if (tanggalMulaiStr != null) {
                LocalDate tanggalMulai = parseISODateTimeToLocalDate(tanggalMulaiStr);
                event.setTanggalMulai(tanggalMulai);
            }
        }
        if (eventNode.has("tanggalSelesai")) {
            String tanggalSelesaiStr = eventNode.get("tanggalSelesai").asText(null);
            if (tanggalSelesaiStr != null) {
                LocalDate tanggalSelesai = parseISODateTimeToLocalDate(tanggalSelesaiStr);
                event.setTanggalSelesai(tanggalSelesai);
            }
        }
        
        // Handle kategori ID
        if (eventNode.has("idKategori") && !eventNode.get("idKategori").isNull()) {
            Long kategoriId = eventNode.get("idKategori").asLong();
            log.info("Setting kategori with ID: {}", kategoriId);
            Category kategori = categoryService.getCategoryById(kategoriId);
            event.setKategori(kategori);
        } else if (eventNode.has("kategori") && eventNode.get("kategori").has("idKategori")) {
            Long kategoriId = eventNode.get("kategori").get("idKategori").asLong();
            log.info("Setting kategori with nested ID: {}", kategoriId);
            Category kategori = categoryService.getCategoryById(kategoriId);
            event.setKategori(kategori);
        }
        
        // Handle venue ID
        if (eventNode.has("idVenue") && !eventNode.get("idVenue").isNull()) {
            Long venueId = eventNode.get("idVenue").asLong();
            log.info("Setting venue with ID: {}", venueId);
            Venue venue = venueService.getVenueById(venueId);
            event.setVenue(venue);
        } else if (eventNode.has("venue") && eventNode.get("venue").has("idVenue")) {
            Long venueId = eventNode.get("venue").get("idVenue").asLong();
            log.info("Setting venue with nested ID: {}", venueId);
            Venue venue = venueService.getVenueById(venueId);
            event.setVenue(venue);
        }
        
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
    
    /**
     * Helper method to parse ISO 8601 datetime string to LocalDate
     * Handles both ISO datetime format (with time) and date-only format
     */
    private LocalDate parseISODateTimeToLocalDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        
        try {
            // Handle ISO 8601 with timezone (e.g., "2025-12-02T14:30:00Z" or "2025-12-02T09:23:00.000Z")
            if (dateString.contains("Z") || dateString.contains("+")) {
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString);
                return zonedDateTime.toLocalDate();
            }
            // Try parsing as ISO datetime first (e.g., "2025-12-02T14:30:00")
            else if (dateString.contains("T")) {
                LocalDateTime dateTime = LocalDateTime.parse(dateString);
                return dateTime.toLocalDate();
            } 
            // Try parsing as date-only format (e.g., "2025-12-02")
            else {
                return LocalDate.parse(dateString);
            }
        } catch (Exception e) {
            log.error("Error parsing date string: {}", dateString, e);
            throw new IllegalArgumentException("Invalid date format: " + dateString, e);
        }
    }
}
