package com.eventkonser.service;

import com.eventkonser.model.Event;
import com.eventkonser.model.EventStatus;
import com.eventkonser.repository.EventRepository;
import com.eventkonser.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    
    private final EventRepository eventRepository;
    private final TicketService ticketService;
    
    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        // Set starting price for each event
        events.forEach(event -> {
            log.debug("Processing event: {} (ID: {})", event.getNamaEvent(), event.getIdEvent());
            try {
                Double startingPrice = ticketService.getStartingPriceByEvent(event.getIdEvent());
                log.debug("Starting price for event {}: {}", event.getIdEvent(), startingPrice);
                event.setStartingPrice(startingPrice != null ? startingPrice : 0.0);
            } catch (Exception e) {
                log.error("Error getting starting price for event {}: {}", event.getIdEvent(), e.getMessage());
                event.setStartingPrice(0.0);
            }
        });
        return events;
    }
    
    @Transactional(readOnly = true)
    public long countAllEvents() {
        try {
            // Gunakan count query yang lebih efficient daripada loading semua data
            long count = eventRepository.count();
            log.info("Total events count: {}", count);
            return count;
        } catch (Exception e) {
            log.error("Error counting all events: ", e);
            return 0L;
        }
    }
    
    @Transactional(readOnly = true)
    public List<Event> getUpcomingEvents() {
        List<Event> events = eventRepository.findByTanggalMulaiGreaterThanEqualOrderByTanggalMulaiAsc(LocalDate.now());
        // Set starting price for each event
        events.forEach(event -> {
            log.debug("Processing upcoming event: {} (ID: {})", event.getNamaEvent(), event.getIdEvent());
            try {
                Double startingPrice = ticketService.getStartingPriceByEvent(event.getIdEvent());
                log.debug("Starting price for upcoming event {}: {}", event.getIdEvent(), startingPrice);
                event.setStartingPrice(startingPrice != null ? startingPrice : 0.0);
            } catch (Exception e) {
                log.error("Error getting starting price for upcoming event {}: {}", event.getIdEvent(), e.getMessage());
                event.setStartingPrice(0.0);
            }
        });
        return events;
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
        
        // Set starting price for the event
        Double startingPrice = ticketService.getStartingPriceByEvent(id);
        event.setStartingPrice(startingPrice != null ? startingPrice : 0.0);
        
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
        List<Event> events = eventRepository.findPopularEvents();
        // Set starting price for each event
        events.forEach(event -> {
            log.debug("Processing popular event: {} (ID: {})", event.getNamaEvent(), event.getIdEvent());
            try {
                Double startingPrice = ticketService.getStartingPriceByEvent(event.getIdEvent());
                log.debug("Starting price for popular event {}: {}", event.getIdEvent(), startingPrice);
                event.setStartingPrice(startingPrice != null ? startingPrice : 0.0);
            } catch (Exception e) {
                log.error("Error getting starting price for popular event {}: {}", event.getIdEvent(), e.getMessage());
                event.setStartingPrice(0.0);
            }
        });
        return events;
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
        
        log.info("✅ Updated event: ID={}, Kategori={}, Venue={}", 
            id, 
            eventDetails.getKategori() != null ? eventDetails.getKategori().getIdKategori() : "null",
            eventDetails.getVenue() != null ? eventDetails.getVenue().getIdVenue() : "null");
        
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
        try {
            long count = eventRepository.countByTanggalMulaiGreaterThanEqual(LocalDate.now());
            
            // Jika query tidak bekerja, hitung manual
            if (count == 0) {
                List<Event> upcomingList = getUpcomingEvents();
                count = upcomingList.size();
                System.out.println("countUpcomingEvents (manual): " + count);
            } else {
                System.out.println("countUpcomingEvents (query): " + count);
            }
            
            return count;
        } catch (Exception e) {
            System.err.println("Error calculating countUpcomingEvents: " + e.getMessage());
            e.printStackTrace();
            // Fallback ke manual count
            return getUpcomingEvents().size();
        }
    }
    
    @Transactional(readOnly = true)
    public long countEventsByStatus(EventStatus status) {
        return eventRepository.countByStatus(status);
    }
}