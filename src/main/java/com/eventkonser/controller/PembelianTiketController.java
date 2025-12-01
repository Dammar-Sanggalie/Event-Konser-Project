package com.eventkonser.controller;

import com.eventkonser.model.Order;
import com.eventkonser.model.OrderStatus;
import com.eventkonser.service.OrderService;
import com.eventkonser.dto.ApiResponse;
import com.eventkonser.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller untuk Pembelian Tiket (Alias untuk OrderController)
 * Frontend menggunakan endpoint ini untuk fetch orders dengan berbagai filter
 * 
 * Endpoint base: /api/pembelian-tiket
 */
@RestController
@RequestMapping("/api/pembelian-tiket")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PembelianTiketController {
    
    private final OrderService orderService;
    
    /**
     * GET /api/pembelian-tiket/user/{userId} - Get user orders dengan optional status filter
     * 
     * Query parameters:
     * - status: PAID, PENDING, CANCELLED, USED (optional)
     * 
     * Contoh:
     * GET /api/pembelian-tiket/user/123?status=PAID
     * GET /api/pembelian-tiket/user/123?status=PENDING
     * GET /api/pembelian-tiket/user/123 (return all)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Order>>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(name = "status", required = false) String status) {
        
        try {
            List<Order> orders;
            
            if (status != null && !status.trim().isEmpty()) {
                try {
                    OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                    orders = orderService.getOrdersByUserAndStatus(userId, orderStatus);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid status. Allowed values: PAID, PENDING, CANCELLED, USED"));
                }
            } else {
                // Return all orders for user
                orders = orderService.getOrdersByUserWithDetails(userId);
            }
            
            // Enrich orders with event data from ticket if missing
            orders.forEach(order -> {
                if ((order.getEventName() == null || order.getEventName().equals("Event Information Unavailable")) && 
                    order.getTicket() != null && order.getTicket().getEvent() != null) {
                    order.setEventName(order.getTicket().getEvent().getNamaEvent());
                    order.setEventDate(order.getTicket().getEvent().getTanggalMulai() != null ? 
                        order.getTicket().getEvent().getTanggalMulai().toString() : "");
                    order.setEventImageUrl(order.getTicket().getEvent().getPosterUrl());
                    order.setIdEvent(order.getTicket().getEvent().getIdEvent());
                    if (order.getTicket().getEvent().getVenue() != null) {
                        order.setVenueName(order.getTicket().getEvent().getVenue().getNamaVenue());
                    }
                }
            });
            
            return ResponseEntity.ok(ApiResponse.success("Success", orders));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error fetching orders: " + e.getMessage()));
        }
    }
    
    /**
     * GET /api/pembelian-tiket/{id} - Get order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);
            return ResponseEntity.ok(ApiResponse.success("Success", order));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
