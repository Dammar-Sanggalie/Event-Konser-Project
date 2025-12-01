package com.eventkonser.service;

import com.eventkonser.model.Ticket;
import com.eventkonser.model.TicketStatus;
import com.eventkonser.repository.TicketRepository;
import com.eventkonser.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {
    
    private final TicketRepository ticketRepository;
    
    @Transactional(readOnly = true)
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAllWithEvent();
    }
    
    @Transactional(readOnly = true)
    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tiket tidak ditemukan dengan ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByEvent(Long eventId) {
        return ticketRepository.findByEvent_IdEventOrderByHargaAsc(eventId);
    }
    
    @Transactional(readOnly = true)
    public List<Ticket> getAvailableTicketsByEvent(Long eventId) {
        return ticketRepository.findByEvent_IdEventAndStatusOrderByHargaAsc(eventId);
    }
    
    @Transactional(readOnly = true)
    public Double getStartingPriceByEvent(Long eventId) {
        log.debug("Getting starting price for event ID: {}", eventId);
        Double price = ticketRepository.findMinimumPriceByEvent(eventId);
        log.debug("Found starting price for event {}: {}", eventId, price);
        return price;
    }
    
    @Transactional
    public Ticket createTicket(Ticket ticket) {
        // Set stok awal
        if (ticket.getStokAwal() == null) {
            ticket.setStokAwal(ticket.getStok());
        }
        return ticketRepository.save(ticket);
    }
    
    @Transactional
    public Ticket updateTicket(Long id, Ticket ticketDetails) {
        Ticket ticket = getTicketById(id);
        ticket.setJenisTiket(ticketDetails.getJenisTiket());
        ticket.setHarga(ticketDetails.getHarga());
        ticket.setStok(ticketDetails.getStok());
        ticket.setDeskripsi(ticketDetails.getDeskripsi());
        ticket.setMaxPembelian(ticketDetails.getMaxPembelian());
        ticket.setStatus(ticketDetails.getStatus());
        return ticketRepository.save(ticket);
    }
    
    @Transactional
    public void deleteTicket(Long id) {
        Ticket ticket = getTicketById(id);
        ticketRepository.delete(ticket);
    }
    
    @Transactional(readOnly = true)
    public Double getTotalRevenueByEvent(Long eventId) {
        Double revenue = ticketRepository.getTotalRevenueByEvent(eventId);
        return revenue != null ? revenue : 0.0;
    }
    
    @Transactional(readOnly = true)
    public Long countSoldTicketsByEvent(Long eventId) {
        Long count = ticketRepository.countSoldTicketsByEvent(eventId);
        return count != null ? count : 0L;
    }
    
    @Transactional(readOnly = true)
    public Double getMinimumPriceByEvent(Long eventId) {
        return ticketRepository.findMinimumPriceByEvent(eventId);
    }
}