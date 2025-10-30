package com.eventkonser.repository;

import com.eventkonser.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    
    // Search by name
    List<Artist> findByNamaArtisContainingIgnoreCase(String keyword);
    
    // Find by genre
    List<Artist> findByGenre(String genre);
    
    // Find by country
    List<Artist> findByNegaraAsal(String negara);
    
    // Get all genres (distinct)
    @Query("SELECT DISTINCT a.genre FROM Artist a ORDER BY a.genre")
    List<String> findAllGenres();
    
    // FIXED: Get popular artists (by event count)
    // Karena relasi ManyToMany ada di Event.artists, kita query dari Event
    @Query("SELECT a, COUNT(e) as eventCount FROM Event e " +
           "JOIN e.artists a " +
           "GROUP BY a.idArtis " +
           "ORDER BY eventCount DESC")
    List<Object[]> findPopularArtists();
}