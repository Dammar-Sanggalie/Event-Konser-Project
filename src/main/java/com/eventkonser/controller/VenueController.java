package com.eventkonser.controller;

import com.eventkonser.model.Venue;
import com.eventkonser.service.VenueService;
import com.eventkonser.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VenueController {
    
    private final VenueService venueService;
    
    /**
     * GET /api/venues - Get all venues
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Venue>>> getAllVenues() {
        return ResponseEntity.ok(ApiResponse.success("Success", venueService.getAllVenues()));
    }
    
    /**
     * GET /api/venues/{id} - Get venue by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Venue>> getVenueById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Success", venueService.getVenueById(id)));
    }
    
    /**
     * GET /api/venues/city/{city} - Get venues by city
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<List<Venue>>> getVenuesByCity(@PathVariable String city) {
        return ResponseEntity.ok(ApiResponse.success("Success", venueService.getVenuesByCity(city)));
    }
    
    /**
     * GET /api/venues/available - Get available venues by date range
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<Venue>>> getAvailableVenues(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(ApiResponse.success("Success", venueService.getAvailableVenues(startDate, endDate)));
    }
    
    /**
     * GET /api/venues/cities - Get all cities
     */
    @GetMapping("/cities")
    public ResponseEntity<ApiResponse<List<String>>> getAllCities() {
        return ResponseEntity.ok(ApiResponse.success("Success", venueService.getAllCities()));
    }
    
    /**
     * POST /api/venues - Create new venue (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<Venue>> createVenue(@RequestBody Venue venue) {
        return ResponseEntity.ok(ApiResponse.success("Venue berhasil dibuat", venueService.createVenue(venue)));
    }
    
    /**
     * PUT /api/venues/{id} - Update venue (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Venue>> updateVenue(
            @PathVariable Long id,
            @RequestBody Venue venueDetails) {
        return ResponseEntity.ok(ApiResponse.success("Venue berhasil diupdate", venueService.updateVenue(id, venueDetails)));
    }
    
    /**
     * DELETE /api/venues/{id} - Delete venue (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.ok(ApiResponse.success("Venue berhasil dihapus", null));
    }
}