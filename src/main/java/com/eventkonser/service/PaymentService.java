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
        
        // Get or create payment (in case it wasn't created during booking)
        Payment payment = paymentRepository.findByOrder_IdPembelian(orderId)
            .orElseGet(() -> {
                Payment newPayment = new Payment();
                newPayment.setOrder(order);
                newPayment.setJumlahBayar(order.getTotalHarga());
                newPayment.setStatusPembayaran(PaymentStatus.PENDING);
                newPayment.setExpiredAt(LocalDateTime.now().plusMinutes(15));
                return newPayment;
            });
        
        // Simulasi payment gateway processing
        // In production: redirect to Midtrans/Xendit, handle callback, update status
        // For now: Mark as SUCCESS after validation
        
        // Update payment
        payment.setMetodePembayaran(paymentMethod);
        payment.setTanggalBayar(LocalDateTime.now());
        payment.setStatusPembayaran(PaymentStatus.SUCCESS);
        
        // Update order
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        
        return paymentRepository.save(payment);
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