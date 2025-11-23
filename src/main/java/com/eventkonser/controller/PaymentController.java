package com.eventkonser.controller;

import com.eventkonser.model.Payment;
import com.eventkonser.service.PaymentService;
import com.eventkonser.dto.ApiResponse;
import com.eventkonser.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

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
     * POST /api/payments/create-snap-token - Create Midtrans Snap token
     * Frontend call this to get snap token for Midtrans Snap UI
     */
    @PostMapping("/create-snap-token")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createSnapToken(@RequestParam Long orderId) {
        try {
            Map<String, Object> snapData = paymentService.createSnapToken(orderId);
            return ResponseEntity.ok(ApiResponse.success("Snap token created successfully", snapData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("Failed to create snap token: " + e.getMessage())
            );
        }
    }
    
    /**
     * POST /api/payments/callback - Midtrans notification callback
     * Midtrans kirim webhook ke endpoint ini untuk notify payment status
     */
    @PostMapping("/callback")
    public ResponseEntity<String> handleMidtransCallback(@RequestBody Map<String, Object> notification) {
        try {
            paymentService.handleMidtransCallback(notification);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing callback");
        }
    }
    
    /**
     * POST /api/payments/process - Process payment (fallback untuk simulasi/testing)
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
     * POST /api/payments/callback - Midtrans notification callback
     * Midtrans kirim webhook ke endpoint ini untuk notify payment status
     */
    @PostMapping("/callback")
    public ResponseEntity<String> handleMidtransCallback(@RequestBody Map<String, Object> notification) {
        try {
            paymentService.handleMidtransCallback(notification);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing callback");
        }
    }
}