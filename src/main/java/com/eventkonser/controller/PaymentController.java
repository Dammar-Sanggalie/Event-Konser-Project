package com.eventkonser.controller;

import com.eventkonser.model.Payment;
import com.eventkonser.service.PaymentService;
import com.eventkonser.service.MockPaymentService;
import com.eventkonser.dto.ApiResponse;
import com.eventkonser.dto.PaymentRequest;

import com.eventkonser.dto.PaymentAnalyticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {
    
    private final PaymentService paymentService;
    private final MockPaymentService mockPaymentService;
    
    /**
     * GET /api/payments - Get all payments (admin only)
     * Return PaymentAnalyticsResponse for analytics dashboard (no circular refs)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentAnalyticsResponse>>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        List<PaymentAnalyticsResponse> analyticsData = payments.stream()
            .map(payment -> PaymentAnalyticsResponse.builder()
                .idPembayaran(payment.getIdPembayaran())
                .idPembelian(payment.getOrder() != null ? payment.getOrder().getIdPembelian() : null)
                .jumlahBayar(payment.getJumlahBayar())
                .metodePembayaran(payment.getMetodePembayaran())
                .statusPembayaran(payment.getStatusPembayaran() != null ? payment.getStatusPembayaran().toString() : null)
                .tanggalBayar(payment.getTanggalBayar())
                .createdAt(payment.getCreatedAt())
                .expiredAt(payment.getExpiredAt())
                .paymentUrl(payment.getPaymentUrl())
                .paymentGatewayId(payment.getPaymentGatewayId())
                .notes(payment.getNotes())
                .build())
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Success", analyticsData));
    }
    
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
    public ResponseEntity<ApiResponse<Map<String, Object>>> processMockPayment(
            @RequestParam Long orderId,
            @RequestParam(required = false) String paymentMethod) {
        try {
            Map<String, Object> result = paymentService.processPaymentWithMock(orderId, paymentMethod);
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