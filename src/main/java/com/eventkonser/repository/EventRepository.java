package com.eventkonser.repository;

import com.eventkonser.model.Event;
import com.eventkonser.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    // Find upcoming events
    List<Event> findByTanggalMulaiGreaterThanEqualOrderByTanggalMulaiAsc(LocalDate date);
    
    // Find by status
    List<Event> findByStatus(EventStatus status);
    
    // Search by name
    List<Event> findByNamaEventContainingIgnoreCase(String keyword);
    
    // Find by category
    List<Event> findByKategori_IdKategori(Long categoryId);
    
    // Find by city (through venue)
    @Query("SELECT e FROM Event e WHERE LOWER(e.venue.kota) = LOWER(:kota) ORDER BY e.tanggalMulai ASC")
    List<Event> findByVenue_KotaIgnoreCase(@Param("kota") String kota);
    
    // Find by artist
    @Query("SELECT DISTINCT e FROM Event e JOIN e.artists a WHERE a.idArtis = :artistId AND e.tanggalMulai >= :currentDate")
    List<Event> findByArtist(@Param("artistId") Long artistId, @Param("currentDate") LocalDate currentDate);
    
    // Search by artist name
    @Query("SELECT DISTINCT e FROM Event e JOIN e.artists a WHERE LOWER(a.namaArtis) LIKE LOWER(CONCAT('%', :artistName, '%')) AND e.tanggalMulai >= :currentDate")
    List<Event> findByArtistName(@Param("artistName") String artistName, @Param("currentDate") LocalDate currentDate);
    
    // Find by date range
    @Query("SELECT e FROM Event e WHERE e.tanggalMulai BETWEEN :startDate AND :endDate ORDER BY e.tanggalMulai ASC")
    List<Event> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Complex filter (category, city, date range)
    @Query("SELECT e FROM Event e WHERE " +
           "(:categoryId IS NULL OR e.kategori.idKategori = :categoryId) AND " +
           "(:kota IS NULL OR e.venue.kota = :kota) AND " +
           "e.tanggalMulai BETWEEN :startDate AND :endDate " +
           "ORDER BY e.tanggalMulai ASC")
    List<Event> findByFilters(@Param("categoryId") Long categoryId,
                               @Param("kota") String kota,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);
    
    // Get event with all relationships (optimize query)
    // Split into two queries to avoid MultipleBagFetchException
    @Query("SELECT DISTINCT e FROM Event e " +
           "LEFT JOIN FETCH e.kategori " +
           "LEFT JOIN FETCH e.venue " +
           "LEFT JOIN FETCH e.artists " +
           "WHERE e.idEvent = :eventId")
    Optional<Event> findByIdWithDetails(@Param("eventId") Long eventId);
    
    // Separate query for tickets to avoid MultipleBagFetchException
    @Query("SELECT DISTINCT e FROM Event e " +
           "LEFT JOIN FETCH e.tickets " +
           "WHERE e.idEvent = :eventId")
    Optional<Event> findByIdWithTickets(@Param("eventId") Long eventId);

    /**
     * TAMBAHKAN METHOD INI:
     * Mengambil semua event beserta relasi utamanya untuk menghindari LazyInitializationException.
     */
    @Query("SELECT DISTINCT e FROM Event e " +
           "LEFT JOIN FETCH e.kategori " +
           "LEFT JOIN FETCH e.venue " +
           "LEFT JOIN FETCH e.artists")
    List<Event> findAllWithDetails();
    
    // Get popular events (by ticket sales)
    @Query(value = "SELECT e.*, COUNT(o.id_pembelian) as total_sold " +
           "FROM event e " +
           "LEFT JOIN tiket t ON e.id_event = t.id_event " +
           "LEFT JOIN pembelian_tiket o ON t.id_tiket = o.id_tiket AND o.status = 'PAID' " +
           "WHERE e.tanggal_mulai >= CURDATE() " +
           "GROUP BY e.id_event " +
           "ORDER BY total_sold DESC " +
           "LIMIT 10", 
           nativeQuery = true)
    List<Event> findPopularEvents();
    
    // Count events by status
    long countByStatus(EventStatus status);
    
    // Count upcoming events
    long countByTanggalMulaiGreaterThanEqual(LocalDate date);
}