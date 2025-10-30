package com.eventkonser.repository;

import com.eventkonser.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    
    // Find by city
    List<Venue> findByKota(String kota);
    
    // Search venues
    List<Venue> findByNamaVenueContainingIgnoreCase(String keyword);
    
    // Find venues with capacity greater than
    List<Venue> findByKapasitasGreaterThanEqual(Integer minCapacity);
    
    // Get all cities (distinct)
    @Query("SELECT DISTINCT v.kota FROM Venue v ORDER BY v.kota")
    List<String> findAllCities();
    
    // Check venue availability by date
    @Query("SELECT v FROM Venue v WHERE v.idVenue NOT IN " +
           "(SELECT e.venue.idVenue FROM Event e WHERE " +
           "e.tanggalMulai <= :endDate AND e.tanggalSelesai >= :startDate)")
    List<Venue> findAvailableVenues(@Param("startDate") java.time.LocalDate startDate, 
                                     @Param("endDate") java.time.LocalDate endDate);
}
