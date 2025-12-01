package com.eventkonser.controller;

import com.eventkonser.model.Sponsor;
import com.eventkonser.service.SponsorService;
import com.eventkonser.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sponsors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SponsorController {
    
    private final SponsorService sponsorService;
    
    /**
     * GET /api/sponsors - Get all sponsors (Admin)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Sponsor>>> getAllSponsors() {
        return ResponseEntity.ok(ApiResponse.success("Success", sponsorService.getAllSponsors()));
    }
    
    /**
     * GET /api/sponsors/event/{eventId} - Get sponsors by event
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<List<Sponsor>>> getSponsorsByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(ApiResponse.success("Success", sponsorService.getSponsorsByEvent(eventId)));
    }
    
    /**
     * POST /api/sponsors - Create new sponsor (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<Sponsor>> createSponsor(@RequestBody Sponsor sponsor) {
        return ResponseEntity.ok(ApiResponse.success("Sponsor berhasil ditambahkan", sponsorService.createSponsor(sponsor)));
    }
    
    /**
     * PUT /api/sponsors/{id} - Update sponsor (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Sponsor>> updateSponsor(
            @PathVariable Long id,
            @RequestBody Sponsor sponsorDetails) {
        return ResponseEntity.ok(ApiResponse.success("Sponsor berhasil diupdate", sponsorService.updateSponsor(id, sponsorDetails)));
    }
    
    /**
     * DELETE /api/sponsors/{id} - Delete sponsor (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSponsor(@PathVariable Long id) {
        sponsorService.deleteSponsor(id);
        return ResponseEntity.ok(ApiResponse.success("Sponsor berhasil dihapus", null));
    }
}