package com.eventkonser.controller;

import com.eventkonser.model.Order;
import com.eventkonser.model.OrderStatus;
import com.eventkonser.model.PaymentStatus;
import com.eventkonser.service.OrderService;
import com.eventkonser.service.PaymentService;
import com.eventkonser.dto.ApiResponse;
import com.eventkonser.dto.BookingRequest;
import com.eventkonser.dto.UpdateOrderStatusRequest;
import com.eventkonser.dto.OrderAnalyticsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {
    
    private final OrderService orderService;
    private final PaymentService paymentService;
    
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
            log.error("Error in bookTicket: {}", e.getMessage(), e);
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
    public ResponseEntity<ApiResponse<List<OrderAnalyticsResponse>>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderAnalyticsResponse> analyticsData = orders.stream()
            .map(order -> {
                OrderAnalyticsResponse response = OrderAnalyticsResponse.builder()
                    .idPembelian(order.getIdPembelian())
                    .idPengguna(order.getUser() != null ? order.getUser().getIdPengguna() : null)
                    .userName(order.getUser() != null ? order.getUser().getNama() : null)
                    .userEmail(order.getUser() != null ? order.getUser().getEmail() : null)
                    .userPhone(order.getUser() != null ? order.getUser().getNoHp() : null)
                    .jumlah(order.getJumlah())
                    .totalHarga(order.getTotalHarga())
                    .subtotal(order.getSubtotal())
                    .discountAmount(order.getDiscountAmount())
                    .status(order.getStatus() != null ? order.getStatus().toString() : null)
                    .tanggalPembelian(order.getTanggalPembelian())
                    .ticketId(order.getTicket() != null ? order.getTicket().getIdTiket() : null)
                    .ticketType(order.getTicket() != null ? order.getTicket().getJenisTiket() : null)
                    .build();
                
                // Add event information if available
                if (order.getTicket() != null && order.getTicket().getEvent() != null) {
                    response.setEventName(order.getTicket().getEvent().getNamaEvent());
                } else {
                    response.setEventName(order.getEventName());
                }
                
                // Add payment information
                if (order.getPayment() != null) {
                    response.setStatusPembayaran(order.getPayment().getStatusPembayaran() != null ? 
                        order.getPayment().getStatusPembayaran().toString() : null);
                    response.setTanggalBayar(order.getPayment().getTanggalBayar());
                }
                
                return response;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Success", analyticsData));
    }
    
    /**
     * PATCH /api/orders/{orderId}/status - Update order status (Admin only)
     */
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {
        try {
            if (!request.containsKey("status")) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Status is required"));
            }
            
            String status = request.get("status").toUpperCase();
            Order order = orderService.getOrderById(orderId);
            
            if (order == null) {
                return ResponseEntity.notFound().build();
            }
            
            try {
                OrderStatus newStatus = OrderStatus.valueOf(status);
                Order updatedOrder = orderService.updateOrderStatus(orderId, newStatus);
                return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", updatedOrder));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid status. Allowed: PENDING, PAID, CANCELLED, COMPLETED"));
            }
            
        } catch (Exception e) {
            log.error("Error updating order status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update order status: " + e.getMessage()));
        }
    }
    
    /**
     * PUT /api/orders/{id} - Update order and payment status (Admin only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest request) {
        try {
            // Update order status jika ada
            if (request.getOrderStatus() != null && !request.getOrderStatus().isEmpty()) {
                try {
                    OrderStatus orderStatus = OrderStatus.valueOf(request.getOrderStatus().toUpperCase());
                    orderService.updateOrderStatus(id, orderStatus);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid order status. Allowed: PENDING, PAID, USED, CANCELLED, EXPIRED, REFUNDED"));
                }
            }
            
            // Update payment status jika ada
            if (request.getStatusPembayaran() != null && !request.getStatusPembayaran().isEmpty()) {
                try {
                    PaymentStatus paymentStatus = PaymentStatus.valueOf(request.getStatusPembayaran().toUpperCase());
                    paymentService.updatePaymentStatus(id, paymentStatus);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid payment status. Allowed: PENDING, SUCCESS, FAILED, EXPIRED, REFUNDED"));
                }
            }
            
            Order updatedOrder = orderService.getOrderById(id);
            return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", updatedOrder));
        } catch (Exception e) {
            log.error("Error updating order status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update order: " + e.getMessage()));
        }
    }
}