package com.eventkonser.repository;

import com.eventkonser.model.Ticket;
import com.eventkonser.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    // Find by event
    List<Ticket> findByEvent_IdEventOrderByHargaAsc(Long eventId);
    
    // Find available tickets for event
    List<Ticket> findByEvent_IdEventAndStatusOrderByHargaAsc(Long eventId, TicketStatus status);
    
    // Find by ticket type
    List<Ticket> findByJenisTiket(String jenisTiket);
    
    // Get ticket with pessimistic lock (untuk booking - prevent race condition)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.idTiket = :ticketId")
    Optional<Ticket> findByIdWithLock(@Param("ticketId") Long ticketId);
    
    // Check ticket availability
    @Query("SELECT t FROM Ticket t WHERE t.idTiket = :ticketId AND t.stok >= :quantity AND t.status = 'AVAILABLE'")
    Optional<Ticket> findAvailableTicket(@Param("ticketId") Long ticketId, @Param("quantity") Integer quantity);
    
    // Get total revenue by event
    @Query("SELECT SUM(o.totalHarga) FROM Order o WHERE o.ticket.event.idEvent = :eventId AND o.status = 'PAID'")
    Double getTotalRevenueByEvent(@Param("eventId") Long eventId);
    
    // Count sold tickets by event
    @Query("SELECT SUM(o.jumlah) FROM Order o WHERE o.ticket.event.idEvent = :eventId AND o.status = 'PAID'")
    Long countSoldTicketsByEvent(@Param("eventId") Long eventId);
}