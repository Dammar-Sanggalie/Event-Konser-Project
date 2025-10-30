package com.eventkonser.repository;

import com.eventkonser.model.Order;
import com.eventkonser.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Find by user
    List<Order> findByUser_IdPenggunaOrderByTanggalPembelianDesc(Long userId);
    
    // Find by QR code (untuk check-in)
    Optional<Order> findByQrCode(String qrCode);
    
    // Find by status
    List<Order> findByStatus(OrderStatus status);
    
    // Find by user and status
    List<Order> findByUser_IdPenggunaAndStatusOrderByTanggalPembelianDesc(Long userId, OrderStatus status);
    
    // Find expired pending orders
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' AND o.expiredAt < :now")
    List<Order> findExpiredOrders(@Param("now") LocalDateTime now);
    
    // Find orders by event
    @Query("SELECT o FROM Order o WHERE o.ticket.event.idEvent = :eventId ORDER BY o.tanggalPembelian DESC")
    List<Order> findByEvent(@Param("eventId") Long eventId);
    
    // Get user order history with details
    @Query("SELECT o FROM Order o " +
           "LEFT JOIN FETCH o.ticket t " +
           "LEFT JOIN FETCH t.event e " +
           "LEFT JOIN FETCH o.payment " +
           "WHERE o.user.idPengguna = :userId " +
           "ORDER BY o.tanggalPembelian DESC")
    List<Order> findByUserWithDetails(@Param("userId") Long userId);
    
    // Count orders by status
    long countByStatus(OrderStatus status);
    
    // Get total revenue
    @Query("SELECT SUM(o.totalHarga) FROM Order o WHERE o.status = 'PAID'")
    Double getTotalRevenue();
    
    // Get revenue by date range
    @Query("SELECT SUM(o.totalHarga) FROM Order o WHERE o.status = 'PAID' AND o.tanggalPembelian BETWEEN :startDate AND :endDate")
    Double getRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get daily sales statistics
    @Query("SELECT DATE(o.tanggalPembelian) as date, COUNT(o) as total_orders, SUM(o.totalHarga) as total_revenue " +
           "FROM Order o WHERE o.status = 'PAID' AND o.tanggalPembelian >= :startDate " +
           "GROUP BY DATE(o.tanggalPembelian) " +
           "ORDER BY date DESC")
    List<Object[]> getDailySalesStats(@Param("startDate") LocalDateTime startDate);
}