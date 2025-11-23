package com.eventkonser.service;

import com.eventkonser.model.*;
import com.eventkonser.repository.*;
import com.eventkonser.exception.*;
import com.eventkonser.config.MidtransConfig;
import com.eventkonser.config.AppConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final MidtransConfig midtransConfig;
    private final AppConfig appConfig;
    
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
        
        // Update payment method
        payment.setMetodePembayaran(paymentMethod);
        
        return paymentRepository.save(payment);
    }
    
    /**
     * Create Snap Token untuk Midtrans payment gateway
     */
    @Transactional
    public Map<String, Object> createSnapToken(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order tidak ditemukan dengan ID: " + orderId));
        
        Payment payment = paymentRepository.findByOrder_IdPembelian(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment tidak ditemukan untuk Order ID: " + orderId));
        
        try {
            // Build unique order ID
            String uniqueOrderId = "ORDER-" + orderId + "-" + System.currentTimeMillis();
            
            // Build transaction details
            Map<String, Object> transactionDetails = new HashMap<>();
            transactionDetails.put("order_id", uniqueOrderId);
            transactionDetails.put("gross_amount", payment.getJumlahBayar().longValue());
            
            // Build customer details
            Map<String, String> customerDetails = new HashMap<>();
            customerDetails.put("first_name", order.getUser().getNama());
            customerDetails.put("email", order.getUser().getEmail());
            customerDetails.put("phone", order.getUser().getNoHp() != null ? order.getUser().getNoHp() : "62");
            
            // Build item details
            Map<String, Object> item = new HashMap<>();
            item.put("id", "ITEM-" + orderId);
            item.put("price", payment.getJumlahBayar().longValue());
            item.put("quantity", 1);
            item.put("name", order.getEventName() + " - " + order.getTicketType());
            
            List<Map<String, Object>> itemDetails = new java.util.ArrayList<>();
            itemDetails.add(item);
            
            // Build request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("transaction_details", transactionDetails);
            requestBody.put("customer_details", customerDetails);
            requestBody.put("item_details", itemDetails);
            
            // Create HTTP request to Midtrans
            String auth = Base64.getEncoder().encodeToString(
                (midtransConfig.getServerKey() + ":").getBytes()
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Basic " + auth);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            RestTemplate restTemplate = new RestTemplate();
            
            String midtransUrl = midtransConfig.isProduction() 
                ? "https://app.midtrans.com/snap/v1/transactions"
                : "https://app.sandbox.midtrans.com/snap/v1/transactions";
            
            ResponseEntity<Map> response = restTemplate.postForEntity(midtransUrl, request, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String snapToken = (String) response.getBody().get("token");
                String redirectUrl = (String) response.getBody().get("redirect_url");
                
                // Save gateway ID for callback verification
                if (snapToken != null) {
                    payment.setPaymentGatewayId(snapToken);
                    paymentRepository.save(payment);
                }
                
                Map<String, Object> result = new HashMap<>();
                result.put("snap_token", snapToken);
                result.put("redirect_url", redirectUrl);
                return result;
            }
            
            throw new RuntimeException("Failed to create Midtrans token: " + response.getStatusCode());
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating Midtrans token: " + e.getMessage(), e);
        }
    }
    
    /**
     * Handle Midtrans notification callback
     */
    @Transactional
    public void handleMidtransCallback(Map<String, Object> notification) {
        String orderId = (String) notification.get("order_id");
        String transactionId = (String) notification.get("transaction_id");
        String transactionStatus = (String) notification.get("transaction_status");
        
        if (orderId == null || transactionStatus == null) {
            throw new InvalidRequestException("Invalid notification payload");
        }
        
        // Parse order ID dari format "ORDER-{idPembelian}-{timestamp}"
        String[] parts = orderId.split("-");
        if (parts.length < 2) {
            throw new InvalidRequestException("Invalid order ID format: " + orderId);
        }
        
        try {
            Long paymentId = Long.parseLong(parts[1]);
            Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment tidak ditemukan dengan ID: " + paymentId));
            
            Order order = payment.getOrder();
            
            // Update payment status based on transaction status
            if ("capture".equals(transactionStatus) || "settlement".equals(transactionStatus)) {
                payment.setStatusPembayaran(PaymentStatus.SUCCESS);
                order.setStatus(OrderStatus.PAID);
            } else if ("pending".equals(transactionStatus)) {
                payment.setStatusPembayaran(PaymentStatus.PENDING);
            } else if ("deny".equals(transactionStatus) || "cancel".equals(transactionStatus) || "expire".equals(transactionStatus)) {
                payment.setStatusPembayaran(PaymentStatus.FAILED);
                order.setStatus(OrderStatus.PENDING);
            }
            
            payment.setTanggalBayar(LocalDateTime.now());
            paymentRepository.save(payment);
            orderRepository.save(order);
            
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("Invalid order ID format");
        }
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