package com.eventkonser.repository;

import com.eventkonser.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    
    // Find by user
    @Query("SELECT w FROM Wishlist w " +
           "LEFT JOIN FETCH w.event e " +
           "LEFT JOIN FETCH e.venue " +
           "WHERE w.user.idPengguna = :userId " +
           "ORDER BY w.createdAt DESC")
    List<Wishlist> findByUser(@Param("userId") Long userId);
    
    // Check if user already added event to wishlist
    boolean existsByUser_IdPenggunaAndEvent_IdEvent(Long userId, Long eventId);
    
    // Find specific wishlist item
    Optional<Wishlist> findByUser_IdPenggunaAndEvent_IdEvent(Long userId, Long eventId);
    
    // Count wishlist items by user
    long countByUser_IdPengguna(Long userId);
    
    // Get popular events (by wishlist count)
    @Query("SELECT w.event, COUNT(w) as wishlist_count FROM Wishlist w " +
           "GROUP BY w.event.idEvent " +
           "ORDER BY wishlist_count DESC")
    List<Object[]> findPopularEventsByWishlist();
    
    // Find users to send reminder (event in 1 day)
    @Query("SELECT w FROM Wishlist w WHERE w.reminderSent = false " +
           "AND w.event.tanggalMulai = :tomorrow")
    List<Wishlist> findWishlistsForReminder(@Param("tomorrow") java.time.LocalDate tomorrow);
}
