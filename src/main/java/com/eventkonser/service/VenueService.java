package com.eventkonser.service;

import com.eventkonser.model.Venue;
import com.eventkonser.repository.VenueRepository;
import com.eventkonser.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueService {
    
    private final VenueRepository venueRepository;
    
    @Transactional(readOnly = true)
    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Venue getVenueById(Long id) {
        return venueRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Venue tidak ditemukan dengan ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<Venue> getVenuesByCity(String city) {
        return venueRepository.findByKota(city);
    }
    
    @Transactional(readOnly = true)
    public List<String> getAllCities() {
        return venueRepository.findAllCities();
    }
    
    @Transactional(readOnly = true)
    public List<Venue> searchVenues(String keyword) {
        return venueRepository.findByNamaVenueContainingIgnoreCase(keyword);
    }
    
    @Transactional(readOnly = true)
    public List<Venue> getAvailableVenues(LocalDate startDate, LocalDate endDate) {
        return venueRepository.findAvailableVenues(startDate, endDate);
    }
    
    @Transactional
    public Venue createVenue(Venue venue) {
        return venueRepository.save(venue);
    }
    
    @Transactional
    public Venue updateVenue(Long id, Venue venueDetails) {
        Venue venue = getVenueById(id);
        venue.setNamaVenue(venueDetails.getNamaVenue());
        venue.setAlamat(venueDetails.getAlamat());
        venue.setKapasitas(venueDetails.getKapasitas());
        venue.setKota(venueDetails.getKota());
        return venueRepository.save(venue);
    }
    
    @Transactional
    public void deleteVenue(Long id) {
        Venue venue = getVenueById(id);
        venueRepository.delete(venue);
    }
}