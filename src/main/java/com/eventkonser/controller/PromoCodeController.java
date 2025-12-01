package com.eventkonser.controller;

import com.eventkonser.model.PromoCode;
import com.eventkonser.service.PromoCodeService;
import com.eventkonser.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/promo")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PromoCodeController {
    
    private final PromoCodeService promoCodeService;
    
    /**
     * GET /api/promo - Get all active promo codes
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PromoCode>>> getActivePromoCodes() {
        return ResponseEntity.ok(ApiResponse.success("Success", promoCodeService.getActivePromoCodes(LocalDate.now())));
    }
    
    /**
     * GET /api/promo/validate/{code} - Validate promo code
     */
    @GetMapping("/validate/{code}")
    public ResponseEntity<ApiResponse<PromoCode>> validatePromoCode(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success("Success", promoCodeService.validatePromoCode(code, LocalDate.now())));
    }
    
    /**
     * POST /api/promo - Create new promo code (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<PromoCode>> createPromoCode(@RequestBody PromoCode promoCode) {
        return ResponseEntity.ok(ApiResponse.success("Promo code berhasil dibuat", promoCodeService.createPromoCode(promoCode)));
    }
    
    /**
     * PUT /api/promo/{id} - Update promo code (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PromoCode>> updatePromoCode(
            @PathVariable Long id,
            @RequestBody PromoCode promoCodeDetails) {
        return ResponseEntity.ok(ApiResponse.success("Promo code berhasil diupdate", 
            promoCodeService.updatePromoCode(id, promoCodeDetails)));
    }
    
    /**
     * DELETE /api/promo/{id} - Delete promo code (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePromoCode(@PathVariable Long id) {
        promoCodeService.deletePromoCode(id);
        return ResponseEntity.ok(ApiResponse.success("Promo code berhasil dihapus", null));
    }
}