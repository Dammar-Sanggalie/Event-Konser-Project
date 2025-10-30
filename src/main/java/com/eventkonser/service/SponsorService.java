package com.eventkonser.service;

import com.eventkonser.model.Sponsor;
import com.eventkonser.repository.SponsorRepository;
import com.eventkonser.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SponsorService {
    
    private final SponsorRepository sponsorRepository;
    
    @Transactional(readOnly = true)
    public List<Sponsor> getSponsorsByEvent(Long eventId) {
        return sponsorRepository.findByEvent_IdEvent(eventId);
    }
    
    @Transactional(readOnly = true)
    public Sponsor getSponsorById(Long id) {
        return sponsorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Sponsor tidak ditemukan dengan ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<Sponsor> searchSponsors(String keyword) {
        return sponsorRepository.findByNamaSponsorContainingIgnoreCase(keyword);
    }
    
    @Transactional(readOnly = true)
    public List<Sponsor> getSponsorsByType(String type) {
        return sponsorRepository.findByJenisSponsor(type);
    }
    
    @Transactional(readOnly = true)
    public List<Sponsor> getAllSponsors() {
        return sponsorRepository.findAll();
    }
    
    @Transactional
    public Sponsor createSponsor(Sponsor sponsor) {
        return sponsorRepository.save(sponsor);
    }
    
    @Transactional
    public Sponsor updateSponsor(Long id, Sponsor sponsorDetails) {
        Sponsor sponsor = getSponsorById(id);
        sponsor.setNamaSponsor(sponsorDetails.getNamaSponsor());
        sponsor.setKontak(sponsorDetails.getKontak());
        sponsor.setJenisSponsor(sponsorDetails.getJenisSponsor());
        sponsor.setLogoUrl(sponsorDetails.getLogoUrl());
        return sponsorRepository.save(sponsor);
    }
    
    @Transactional
    public void deleteSponsor(Long id) {
        Sponsor sponsor = getSponsorById(id);
        sponsorRepository.delete(sponsor);
    }
    
    @Transactional
    public void deleteSponsorsByEvent(Long eventId) {
        List<Sponsor> sponsors = getSponsorsByEvent(eventId);
        sponsorRepository.deleteAll(sponsors);
    }
}
