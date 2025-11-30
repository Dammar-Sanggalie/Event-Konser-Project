package com.eventkonser.service;

import com.eventkonser.model.*;
import com.eventkonser.repository.*;
import com.eventkonser.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final MockPaymentService mockPaymentService;
    private final NotificationService notificationService;
    
    @Transactional(readOnly = true)
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payment tidak ditemukan dengan ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrder_IdPembelian(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment tidak ditemukan untuk Order ID: " + orderId));
    }
    
    @Transactional
    public Payment processPayment(Long orderId, String paymentMethod) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order tidak ditemukan dengan ID: " + orderId));
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidRequestException("Order tidak dapat diproses. Status: " + order.getStatus());
        }
        
        // Get or create payment
        Payment payment = paymentRepository.findByOrder_IdPembelian(orderId)
            .orElseGet(() -> {
                Payment newPayment = new Payment();
                newPayment.setOrder(order);
                newPayment.setJumlahBayar(order.getTotalHarga());
                newPayment.setStatusPembayaran(PaymentStatus.PENDING);
                newPayment.setMetodePembayaran("MOCK"); // Default MOCK payment
                newPayment.setExpiredAt(LocalDateTime.now().plusMinutes(15));
                return newPayment;
            });
        
        // Update payment method
        payment.setMetodePembayaran(paymentMethod);
        
        return paymentRepository.save(payment);
    }
    
    /**
     * Process payment menggunakan Mock Payment Service
     * Ini adalah method yang dipanggil dari frontend saat user klik "Complete Payment"
     * 
     * Mock payment instantly mark order sebagai PAID tanpa external service call
     */
    @Transactional
    public java.util.Map<String, Object> processPaymentWithMock(Long orderId) {
        // Delegate ke MockPaymentService yang handle instant success
        return mockPaymentService.processMockPayment(orderId);
    }
    
    @Transactional
    public void processExpiredPayments() {
        List<Payment> expiredPayments = paymentRepository.findExpiredPayments(LocalDateTime.now());
        
        for (Payment payment : expiredPayments) {
            payment.setStatusPembayaran(PaymentStatus.EXPIRED);
            paymentRepository.save(payment);
        }
    }
    
    @Transactional(readOnly = true)
    public Double getSuccessRate() {
        Double rate = paymentRepository.getSuccessRate();
        return rate != null ? rate : 0.0;
    }
}