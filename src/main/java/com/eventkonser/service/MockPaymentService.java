package com.eventkonser.service;

import com.eventkonser.model.*;
import com.eventkonser.repository.*;
import com.eventkonser.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock Payment Service untuk development dan testing
 * Ini adalah payment gateway palsu yang instantly mark payment sebagai SUCCESS
 * 
 * Berguna untuk:
 * - Testing flow aplikasi tanpa perlu payment gateway asli
 * - Development dan demo tanpa need real credentials
 * - Dapat test dengan harga apapun tanpa biaya
 */
@Service
@RequiredArgsConstructor
public class MockPaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    
    /**
     * Proses mock payment - instantly mark sebagai SUCCESS
     * 
     * @param orderId - ID order yang akan dibayar
     * @return Map berisi mock payment response
     */
    @Transactional
    public Map<String, Object> processMockPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order tidak ditemukan dengan ID: " + orderId));
        
        Payment payment = paymentRepository.findByOrder_IdPembelian(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment tidak ditemukan untuk Order ID: " + orderId));
        
        // Generate mock transaction ID
        String mockTransactionId = "MOCK-" + orderId + "-" + System.currentTimeMillis();
        
        // INSTANTLY mark payment as SUCCESS (ini yang magic dari mock payment!)
        payment.setStatusPembayaran(PaymentStatus.SUCCESS);
        payment.setPaymentGatewayId(mockTransactionId);
        payment.setTanggalBayar(LocalDateTime.now());
        paymentRepository.save(payment);
        
        // Update order status
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        
        // Simulasi processing delay (optional, untuk feel lebih real)
        try {
            Thread.sleep(500); // Simulate 500ms processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Return response mirip Midtrans (supaya frontend tidak perlu banyak ubah)
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Mock payment processed successfully");
        response.put("transaction_id", mockTransactionId);
        response.put("order_id", orderId);
        response.put("status", "SUCCESS");
        response.put("timestamp", LocalDateTime.now().toString());
        
        return response;
    }
    
    /**
     * Get mock payment info untuk debugging
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getMockPaymentInfo(Long orderId) {
        Payment payment = paymentRepository.findByOrder_IdPembelian(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment tidak ditemukan untuk Order ID: " + orderId));
        
        Map<String, Object> info = new HashMap<>();
        info.put("order_id", orderId);
        info.put("status", payment.getStatusPembayaran().toString());
        info.put("amount", payment.getJumlahBayar());
        info.put("transaction_id", payment.getPaymentGatewayId());
        info.put("payment_date", payment.getTanggalBayar());
        info.put("gateway", "MOCK");
        
        return info;
    }
    
    /**
     * Simulate payment failure (untuk testing error handling)
     * Bisa dipanggil secara manual untuk test failure flow
     */
    @Transactional
    public Map<String, Object> simulatePaymentFailure(Long orderId, String reason) {
        orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order tidak ditemukan dengan ID: " + orderId));
        
        Payment payment = paymentRepository.findByOrder_IdPembelian(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment tidak ditemukan untuk Order ID: " + orderId));
        
        // Mark payment as FAILED
        payment.setStatusPembayaran(PaymentStatus.FAILED);
        payment.setTanggalBayar(LocalDateTime.now());
        paymentRepository.save(payment);
        
        // Order tetap PENDING (tidak berubah ke PAID)
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Mock payment failure simulated: " + reason);
        response.put("status", "FAILED");
        response.put("reason", reason);
        
        return response;
    }
}
