package com.eventkonser.service;

import com.eventkonser.model.Sponsor;
import com.eventkonser.model.Event;
import com.eventkonser.dto.SponsorDTO;
import com.eventkonser.repository.SponsorRepository;
import com.eventkonser.repository.EventRepository;
import com.eventkonser.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SponsorService {
    
    private final SponsorRepository sponsorRepository;
    private final EventRepository eventRepository;
    
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
    public Sponsor createSponsorFromDTO(SponsorDTO dto) {
        if (dto.getIdEvent() == null) {
            throw new IllegalArgumentException("idEvent tidak boleh null");
        }
        
        Event event = eventRepository.findById(dto.getIdEvent())
            .orElseThrow(() -> new ResourceNotFoundException("Event tidak ditemukan dengan ID: " + dto.getIdEvent()));
        
        Sponsor sponsor = new Sponsor();
        sponsor.setEvent(event);
        sponsor.setNamaSponsor(dto.getNamaSponsor());
        sponsor.setKontak(dto.getKontak());
        sponsor.setJenisSponsor(dto.getJenisSponsor());
        sponsor.setLogoUrl(dto.getLogoUrl());
        
        return sponsorRepository.save(sponsor);
    }

    @Transactional
    public Sponsor updateSponsorFromDTO(Long id, SponsorDTO dto) {
        Sponsor sponsor = getSponsorById(id);
        
        if (dto.getIdEvent() != null) {
            Event event = eventRepository.findById(dto.getIdEvent())
                .orElseThrow(() -> new ResourceNotFoundException("Event tidak ditemukan dengan ID: " + dto.getIdEvent()));
            sponsor.setEvent(event);
        }
        
        sponsor.setNamaSponsor(dto.getNamaSponsor());
        sponsor.setKontak(dto.getKontak());
        sponsor.setJenisSponsor(dto.getJenisSponsor());
        sponsor.setLogoUrl(dto.getLogoUrl());
        
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
