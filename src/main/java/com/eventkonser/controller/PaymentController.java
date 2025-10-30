package com.eventkonser.controller;

import com.eventkonser.model.Payment;
import com.eventkonser.service.PaymentService;
import com.eventkonser.dto.ApiResponse;
import com.eventkonser.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    /**
     * GET /api/payments/order/{orderId} - Get payment by order
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<Payment>> getPaymentByOrder(@PathVariable Long orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success("Success", payment));
    }
    
    /**
     * POST /api/payments/process - Process payment
     */
    @PostMapping("/process")
    public ResponseEntity<ApiResponse<Payment>> processPayment(@RequestBody PaymentRequest request) {
        Payment payment = paymentService.processPayment(
            request.getOrderId(),
            request.getPaymentMethod()
        );
        return ResponseEntity.ok(ApiResponse.success("Pembayaran berhasil!", payment));
    }
    
    /**
     * POST /api/payments/callback - Payment gateway callback
     * (Untuk integrasi dengan Midtrans/Xendit)
     */
    @PostMapping("/callback")
    public ResponseEntity<ApiResponse<String>> paymentCallback(@RequestBody String payload) {
        // TODO: Implement payment gateway callback handler
        return ResponseEntity.ok(ApiResponse.success("Callback received", "OK"));
    }
}