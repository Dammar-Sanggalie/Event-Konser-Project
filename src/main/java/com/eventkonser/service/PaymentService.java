package com.eventkonser.service;

import com.eventkonser.model.*;
import com.eventkonser.repository.*;
import com.eventkonser.exception.*;
import com.eventkonser.dto.PaymentDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final MockPaymentService mockPaymentService;
    private final NotificationService notificationService;
    
    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
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
    public java.util.Map<String, Object> processPaymentWithMock(Long orderId, String paymentMethod) {
        // Delegate ke MockPaymentService yang handle instant success
        return mockPaymentService.processMockPayment(orderId, paymentMethod);
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
    
    @Transactional
    public Payment updatePaymentStatus(Long orderId, PaymentStatus newStatus) {
        Payment payment = paymentRepository.findByOrder_IdPembelian(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment tidak ditemukan untuk Order ID: " + orderId));
        payment.setStatusPembayaran(newStatus);
        return paymentRepository.save(payment);
    }
    
    /**
     * Get all payments dengan detail (untuk admin)
     */
    @Transactional(readOnly = true)
    public List<PaymentDetailResponse> getAllPaymentsWithDetails() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(this::convertToDetailResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert Payment entity to PaymentDetailResponse DTO
     */
    private PaymentDetailResponse convertToDetailResponse(Payment payment) {
        PaymentDetailResponse response = new PaymentDetailResponse();
        
        response.setIdPembayaran(payment.getIdPembayaran());
        response.setMetodePembayaran(payment.getMetodePembayaran());
        response.setJumlahBayar(payment.getJumlahBayar());
        response.setStatusPembayaran(payment.getStatusPembayaran().toString());
        response.setTanggalBayar(payment.getTanggalBayar());
        response.setCreatedAt(payment.getCreatedAt());
        response.setPaymentGatewayId(payment.getPaymentGatewayId());
        response.setExpiredAt(payment.getExpiredAt());
        
        // Order info
        if (payment.getOrder() != null) {
            Order order = payment.getOrder();
            response.setIdPembelian(order.getIdPembelian());
            response.setOrderId(order.getIdPembelian());
            response.setEventName(order.getEventName());
            response.setTanggalPembelian(order.getTanggalPembelian());
            response.setOrderStatus(order.getStatus().toString());
            
            // User info
            if (order.getUser() != null) {
                User user = order.getUser();
                response.setIdPengguna(user.getIdPengguna());
                response.setUserNama(user.getNama());
                response.setUserEmail(user.getEmail());
                response.setUserNoHp(user.getNoHp());
            }
        }
        
        return response;
    }
}