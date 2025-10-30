package com.eventkonser.repository;

import com.eventkonser.model.Payment;
import com.eventkonser.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    // Find by order
    Optional<Payment> findByOrder_IdPembelian(Long orderId);
    
    // Find by payment gateway ID
    Optional<Payment> findByPaymentGatewayId(String gatewayId);
    
    // Find by status
    List<Payment> findByStatusPembayaran(PaymentStatus status);
    
    // Find expired pending payments
    @Query("SELECT p FROM Payment p WHERE p.statusPembayaran = 'PENDING' AND p.expiredAt < :now")
    List<Payment> findExpiredPayments(@Param("now") LocalDateTime now);
    
    // Find by payment method
    List<Payment> findByMetodePembayaran(String metodePembayaran);
    
    // Get payment statistics by method
    @Query("SELECT p.metodePembayaran, COUNT(p), SUM(p.jumlahBayar) " +
           "FROM Payment p WHERE p.statusPembayaran = 'SUCCESS' " +
           "GROUP BY p.metodePembayaran")
    List<Object[]> getPaymentStatsByMethod();
    
    // Get success rate
    @Query("SELECT " +
           "(COUNT(CASE WHEN p.statusPembayaran = 'SUCCESS' THEN 1 END) * 100.0 / COUNT(p)) " +
           "FROM Payment p")
    Double getSuccessRate();
}