package com.eventkonser.service;

import com.eventkonser.model.Event;
import com.eventkonser.model.User;
import com.eventkonser.model.Wishlist;
import com.eventkonser.repository.EventRepository;
import com.eventkonser.repository.UserRepository;
import com.eventkonser.repository.WishlistRepository;
import com.eventkonser.exception.DuplicateResourceException;
import com.eventkonser.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {
    
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    
    /**
     * Get all wishlist items for a user
     */
    @Transactional(readOnly = true)
    public List<Wishlist> getWishlistByUser(Long userId) {
        return wishlistRepository.findByUser(userId);
    }
    
    /**
     * Add event to user's wishlist
     */
    @Transactional
    public Wishlist addToWishlist(Long userId, Long eventId) {
        // Check if already exists
        if (wishlistRepository.existsByUser_IdPenggunaAndEvent_IdEvent(userId, eventId)) {
            throw new DuplicateResourceException("Event sudah ada di wishlist");
        }
        
        // Validate user exists
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan dengan ID: " + userId));
        
        // Validate event exists
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event tidak ditemukan dengan ID: " + eventId));
        
        // Create wishlist
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setEvent(event);
        wishlist.setReminderSent(false);
        
        return wishlistRepository.save(wishlist);
    }
    
    /**
     * Remove event from user's wishlist
     */
    @Transactional
    public void removeFromWishlist(Long userId, Long eventId) {
        Wishlist wishlist = wishlistRepository.findByUser_IdPenggunaAndEvent_IdEvent(userId, eventId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Wishlist item tidak ditemukan untuk User ID: " + userId + " dan Event ID: " + eventId
            ));
        
        wishlistRepository.delete(wishlist);
    }
    
    /**
     * Check if event is in user's wishlist
     */
    @Transactional(readOnly = true)
    public boolean isInWishlist(Long userId, Long eventId) {
        return wishlistRepository.existsByUser_IdPenggunaAndEvent_IdEvent(userId, eventId);
    }
    
    /**
     * Count wishlist items for user
     */
    @Transactional(readOnly = true)
    public long countWishlistByUser(Long userId) {
        return wishlistRepository.countByUser_IdPengguna(userId);
    }
    
    /**
     * Get popular events based on wishlist count
     */
    @Transactional(readOnly = true)
    public List<Object[]> getPopularEventsByWishlist() {
        return wishlistRepository.findPopularEventsByWishlist();
    }
    
    /**
     * Send reminder notification for events happening tomorrow
     * (This would be called by a scheduled task)
     */
    @Transactional
    public void sendEventReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Wishlist> wishlistsToRemind = wishlistRepository.findWishlistsForReminder(tomorrow);
        
        for (Wishlist wishlist : wishlistsToRemind) {
            // TODO: Send email/push notification
            // emailService.sendEventReminder(wishlist.getUser(), wishlist.getEvent());
            
            // Mark as sent
            wishlist.setReminderSent(true);
            wishlistRepository.save(wishlist);
        }
    }
    
    /**
     * Clear all wishlist for a user
     */
    @Transactional
    public void clearWishlist(Long userId) {
        List<Wishlist> wishlists = wishlistRepository.findByUser(userId);
        wishlistRepository.deleteAll(wishlists);
    }
    
    /**
     * Get wishlist with upcoming events only
     */
    @Transactional(readOnly = true)
    public List<Wishlist> getUpcomingWishlistByUser(Long userId) {
        List<Wishlist> wishlists = wishlistRepository.findByUser(userId);
        
        // Filter only upcoming events
        return wishlists.stream()
            .filter(w -> w.getEvent().getTanggalMulai().isAfter(LocalDate.now()) || 
                        w.getEvent().getTanggalMulai().isEqual(LocalDate.now()))
            .toList();
    }
}