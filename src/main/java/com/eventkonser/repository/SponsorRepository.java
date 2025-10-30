package com.eventkonser.repository;

import com.eventkonser.model.Sponsor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SponsorRepository extends JpaRepository<Sponsor, Long> {
    
    // Find by event
    List<Sponsor> findByEvent_IdEvent(Long eventId);
    
    // Search by name
    List<Sponsor> findByNamaSponsorContainingIgnoreCase(String keyword);
    
    // Find by sponsor type
    List<Sponsor> findByJenisSponsor(String jenisSponsor);
}