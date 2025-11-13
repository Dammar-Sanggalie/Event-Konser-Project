package com.eventkonser.service;

import com.eventkonser.model.Event;
import com.eventkonser.model.EventStatus;
import com.eventkonser.repository.EventRepository;
import com.eventkonser.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    
    private final EventRepository eventRepository;
    
    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAllWithDetails();
    }
    
    @Transactional(readOnly = true)
    public List<Event> getUpcomingEvents() {
        return eventRepository.findByTanggalMulaiGreaterThanEqualOrderByTanggalMulaiAsc(LocalDate.now());
    }
    
    @Transactional(readOnly = true)
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Event tidak ditemukan dengan ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public Event getEventWithDetails(Long id) {
        // First, fetch event with artists, category, and venue
        Event event = eventRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new ResourceNotFoundException("Event tidak ditemukan dengan ID: " + id));
        
        // Then, fetch tickets separately to avoid MultipleBagFetchException
        eventRepository.findByIdWithTickets(id).ifPresent(e -> {
            event.setTickets(e.getTickets());
        });
        
        return event;
    }
    
    @Transactional(readOnly = true)
    public List<Event> searchEvents(String keyword) {
        return eventRepository.findByNamaEventContainingIgnoreCase(keyword);
    }
    
    @Transactional(readOnly = true)
    public List<Event> getEventsByCategory(Long categoryId) {
        return eventRepository.findByKategori_IdKategori(categoryId);
    }
    
    @Transactional(readOnly = true)
    public List<Event> getEventsByCity(String city) {
        return eventRepository.findByVenue_KotaIgnoreCase(city);
    }
    
    @Transactional(readOnly = true)
    public List<Event> getEventsByArtist(Long artistId) {
        return eventRepository.findByArtist(artistId, LocalDate.now());
    }
    
    @Transactional(readOnly = true)
    public List<Event> searchEventsByArtistName(String artistName) {
        return eventRepository.findByArtistName(artistName, LocalDate.now());
    }
    
    @Transactional(readOnly = true)
    public List<Event> getEventsByDateRange(LocalDate startDate, LocalDate endDate) {
        return eventRepository.findByDateRange(startDate, endDate);
    }
    
    @Transactional(readOnly = true)
    public List<Event> filterEvents(Long categoryId, String city, LocalDate startDate, LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now();
        if (endDate == null) endDate = LocalDate.now().plusYears(1);
        return eventRepository.findByFilters(categoryId, city, startDate, endDate);
    }
    
    @Transactional(readOnly = true)
    public List<Event> getPopularEvents() {
        return eventRepository.findPopularEvents();
    }
    
    @Transactional(readOnly = true)
    public List<Event> getEventsByStatus(EventStatus status) {
        return eventRepository.findByStatus(status);
    }
    
    @Transactional
    public Event createEvent(Event event) {
        // Set default status
        if (event.getStatus() == null) {
            event.setStatus(EventStatus.UPCOMING);
        }
        return eventRepository.save(event);
    }
    
    @Transactional
    public Event updateEvent(Long id, Event eventDetails) {
        Event event = getEventById(id);
        event.setNamaEvent(eventDetails.getNamaEvent());
        event.setDeskripsi(eventDetails.getDeskripsi());
        event.setTanggalMulai(eventDetails.getTanggalMulai());
        event.setTanggalSelesai(eventDetails.getTanggalSelesai());
        event.setPenyelenggara(eventDetails.getPenyelenggara());
        event.setKategori(eventDetails.getKategori());
        event.setVenue(eventDetails.getVenue());
        event.setBannerUrl(eventDetails.getBannerUrl());
        event.setPosterUrl(eventDetails.getPosterUrl());
        event.setStatus(eventDetails.getStatus());
        return eventRepository.save(event);
    }
    
    @Transactional
    public Event updateEventStatus(Long id, EventStatus status) {
        Event event = getEventById(id);
        event.setStatus(status);
        return eventRepository.save(event);
    }
    
    @Transactional
    public void deleteEvent(Long id) {
        Event event = getEventById(id);
        eventRepository.delete(event);
    }
    
    @Transactional(readOnly = true)
    public long countUpcomingEvents() {
        return eventRepository.countByTanggalMulaiGreaterThanEqual(LocalDate.now());
    }
    
    @Transactional(readOnly = true)
    public long countEventsByStatus(EventStatus status) {
        return eventRepository.countByStatus(status);
    }
}