package com.eventkonser.controller;

import com.eventkonser.model.Wishlist;
import com.eventkonser.service.WishlistService;
import com.eventkonser.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WishlistController {
    
    private final WishlistService wishlistService;
    
    /**
     * GET /api/wishlist/user/{userId} - Get user's wishlist
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Wishlist>>> getWishlistByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Success", wishlistService.getWishlistByUser(userId)));
    }
    
    /**
     * GET /api/wishlist/user/{userId}/upcoming - Get upcoming events in wishlist
     */
    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<ApiResponse<List<Wishlist>>> getUpcomingWishlistByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Success", wishlistService.getUpcomingWishlistByUser(userId)));
    }
    
    /**
     * POST /api/wishlist - Add event to wishlist
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Wishlist>> addToWishlist(
            @RequestParam Long userId,
            @RequestParam Long eventId) {
        return ResponseEntity.ok(ApiResponse.success("Event berhasil ditambahkan ke wishlist", 
            wishlistService.addToWishlist(userId, eventId)));
    }
    
    /**
     * DELETE /api/wishlist - Remove event from wishlist
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @RequestParam Long userId,
            @RequestParam Long eventId) {
        wishlistService.removeFromWishlist(userId, eventId);
        return ResponseEntity.ok(ApiResponse.success("Event berhasil dihapus dari wishlist", null));
    }
    
    /**
     * GET /api/wishlist/check - Check if event is in wishlist
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> isInWishlist(
            @RequestParam Long userId,
            @RequestParam Long eventId) {
        return ResponseEntity.ok(ApiResponse.success("Success", wishlistService.isInWishlist(userId, eventId)));
    }
    
    /**
     * GET /api/wishlist/count/{userId} - Count wishlist items
     */
    @GetMapping("/count/{userId}")
    public ResponseEntity<ApiResponse<Long>> countWishlistByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Success", wishlistService.countWishlistByUser(userId)));
    }
}