package com.eventkonser.controller;

import com.eventkonser.service.*;
import com.eventkonser.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {
    
    private final EventService eventService;
    private final OrderService orderService;
    private final UserService userService;
    private final TicketService ticketService;
    
    /**
     * GET /api/admin/dashboard - Get dashboard statistics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Event statistics
        stats.put("totalEvents", eventService.getAllEvents().size());
        stats.put("upcomingEvents", eventService.countUpcomingEvents());
        
        // Order statistics
        stats.put("totalRevenue", orderService.getTotalRevenue());
        stats.put("totalOrders", orderService.getAllOrders().size());
        
        // User statistics
        stats.put("totalUsers", userService.getAllUsers().size());
        
        return ResponseEntity.ok(ApiResponse.success("Success", stats));
    }
    
    /**
     * GET /api/admin/events/{eventId}/revenue - Get event revenue
     */
    @GetMapping("/events/{eventId}/revenue")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEventRevenue(@PathVariable Long eventId) {
        Map<String, Object> data = new HashMap<>();
        data.put("totalRevenue", ticketService.getTotalRevenueByEvent(eventId));
        data.put("ticketsSold", ticketService.countSoldTicketsByEvent(eventId));
        
        return ResponseEntity.ok(ApiResponse.success("Success", data));
    }
}
