package com.eventkonser.controller;

import com.eventkonser.model.Order;
import com.eventkonser.model.OrderStatus;
import com.eventkonser.service.OrderService;
import com.eventkonser.dto.ApiResponse;
import com.eventkonser.dto.BookingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {
    
    private final OrderService orderService;
    
    /**
     * POST /api/orders/book - Book tickets (CRITICAL ENDPOINT)
     * Frontend sends: { idPengguna, totalHarga, items: [{idTiket, jumlah}], discountAmount, promoCode, subtotal }
     */
    @PostMapping("/book")
    public ResponseEntity<ApiResponse<Order>> bookTicket(@RequestBody BookingRequest request) {
        try {
            System.out.println("📌 BookingRequest received: " + request);
            System.out.println("   idPengguna: " + request.getIdPengguna());
            System.out.println("   items: " + request.getItems());
            System.out.println("   subtotal: " + request.getSubtotal());
            System.out.println("   discountAmount: " + request.getDiscountAmount());
            System.out.println("   promoCode: " + request.getPromoCode());
            
            // For now, create order with first ticket as primary
            // TODO: Upgrade to support multiple tickets per order
            if (request.getItems() != null && !request.getItems().isEmpty()) {
                System.out.println("✅ Processing first item...");
                Order order = orderService.bookTicket(
                    request.getIdPengguna(),
                    request.getItems().get(0).getIdTiket(),
                    request.getItems().get(0).getJumlah(),
                    request.getDiscountAmount() != null ? java.math.BigDecimal.valueOf(request.getDiscountAmount()) : null,
                    request.getPromoCode(),
                    request.getSubtotal() != null ? java.math.BigDecimal.valueOf(request.getSubtotal()) : null
                );
                return ResponseEntity.ok(ApiResponse.success("Booking berhasil! Silakan selesaikan pembayaran dalam 15 menit.", order));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error("Items tidak boleh kosong"));
        } catch (Exception e) {
            System.out.println("❌ Error in bookTicket: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * GET /api/orders/user/{userId} - Get user orders
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Order>>> getUserOrders(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUserWithDetails(userId);
        return ResponseEntity.ok(ApiResponse.success("Success", orders));
    }
    
    /**
     * GET /pembelian-tiket/user/{userId} - Get user orders (Alternative endpoint untuk compatibility)
     * Frontend dapat menggunakan endpoint ini untuk fetch orders
     */
    @GetMapping(value = "/user/{userId}", params = "status")
    public ResponseEntity<ApiResponse<List<Order>>> getUserOrdersByStatus(
            @PathVariable Long userId,
            @RequestParam(required = false) String status) {
        List<Order> orders;
        
        if (status != null && !status.isEmpty()) {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status);
                orders = orderService.getOrdersByUserAndStatus(userId, orderStatus);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid status. Allowed values: PENDING, PAID, CANCELLED, USED"));
            }
        } else {
            orders = orderService.getOrdersByUserWithDetails(userId);
        }
        
        return ResponseEntity.ok(ApiResponse.success("Success", orders));
    }
    
    /**
     * GET /api/orders/{id} - Get order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success("Success", order));
    }
    
    /**
     * GET /api/orders/qr/{qrCode} - Get order by QR Code (for check-in)
     */
    @GetMapping("/qr/{qrCode}")
    public ResponseEntity<ApiResponse<Order>> getOrderByQRCode(@PathVariable String qrCode) {
        Order order = orderService.getOrderByQRCode(qrCode);
        return ResponseEntity.ok(ApiResponse.success("Success", order));
    }
    
    /**
     * POST /api/orders/{id}/cancel - Cancel order
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(@PathVariable Long id) {
        Order order = orderService.cancelOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Order berhasil dibatalkan", order));
    }
    
    /**
     * POST /api/orders/checkin - Check-in with QR Code
     */
    @PostMapping("/checkin")
    public ResponseEntity<ApiResponse<Order>> checkIn(@RequestParam String qrCode) {
        Order order = orderService.markAsUsed(qrCode);
        return ResponseEntity.ok(ApiResponse.success("Check-in berhasil!", order));
    }
    
    /**
     * GET /api/orders - Get all orders (Admin only)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success("Success", orders));
    }
}