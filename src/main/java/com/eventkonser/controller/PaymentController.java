package com.eventkonser.controller;

import com.eventkonser.model.Payment;
import com.eventkonser.service.PaymentService;
import com.eventkonser.service.MockPaymentService;
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
    private final MockPaymentService mockPaymentService;
    
    /**
     * GET /api/payments/order/{orderId} - Get payment by order
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<Payment>> getPaymentByOrder(@PathVariable Long orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success("Success", payment));
    }
    
    /**
     * POST /api/payments/process-mock - Process payment menggunakan Mock Payment
     * Frontend call ini saat user klik "Complete Payment"
     * 
     * Mock payment instantly mark order sebagai PAID
     */
    @PostMapping("/process-mock")
    public ResponseEntity<ApiResponse<Map<String, Object>>> processMockPayment(@RequestParam Long orderId) {
        try {
            Map<String, Object> result = paymentService.processPaymentWithMock(orderId);
            return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("Failed to process payment: " + e.getMessage())
            );
        }
    }
    
    /**
     * GET /api/payments/mock-info/{orderId} - Get mock payment info (untuk debugging)
     */
    @GetMapping("/mock-info/{orderId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMockPaymentInfo(@PathVariable Long orderId) {
        try {
            Map<String, Object> info = mockPaymentService.getMockPaymentInfo(orderId);
            return ResponseEntity.ok(ApiResponse.success("Mock payment info", info));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("Failed to get payment info: " + e.getMessage())
            );
        }
    }
    
    /**
     * POST /api/payments/process - Process payment (fallback untuk compatibility)
     */
    @PostMapping("/process")
    public ResponseEntity<ApiResponse<Payment>> processPayment(@RequestBody PaymentRequest request) {
        Payment payment = paymentService.processPayment(
            request.getOrderId(),
            request.getPaymentMethod()
        );
        return ResponseEntity.ok(ApiResponse.success("Pembayaran berhasil!", payment));
    }
}