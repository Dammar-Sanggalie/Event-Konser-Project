package com.eventkonser.controller;

import com.eventkonser.service.*;
import com.eventkonser.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class AdminController {
    
    private final EventService eventService;
    private final OrderService orderService;
    private final UserService userService;
    private final TicketService ticketService;
    
    /**
     * GET /api/admin/dashboard - Get dashboard statistics
     */
    @GetMapping("/dashboard")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        try {
            log.info("Fetching dashboard statistics...");
            Map<String, Object> stats = new HashMap<>();
            
            // Event statistics
            long totalEvents = eventService.countAllEvents();
            long upcomingEvents = eventService.countUpcomingEvents();
            stats.put("totalEvents", totalEvents);
            stats.put("upcomingEvents", upcomingEvents);
            
            log.info("Dashboard Stats - Total Events: {}, Upcoming: {}", totalEvents, upcomingEvents);
            
            // Order statistics
            long totalOrders = orderService.countAllOrders();
            stats.put("totalOrders", totalOrders);
            
            // Revenue - safely handle null
            Double totalRevenue = orderService.getTotalRevenue();
            stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
            
            log.info("Dashboard Stats - Total Orders: {}, Revenue: {}", totalOrders, totalRevenue);
            
            // User statistics
            long totalUsers = userService.countAllUsers();
            stats.put("totalUsers", totalUsers);
            
            log.info("Dashboard Stats - Total Users: {}", totalUsers);
            log.info("Dashboard statistics fetched successfully");
            
            return ResponseEntity.ok(ApiResponse.success("Success", stats));
        } catch (Exception e) {
            log.error("Error in getDashboardStats: ", e);
            
            // Return minimal data instead of error
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("totalEvents", 0L);
            fallback.put("upcomingEvents", 0L);
            fallback.put("totalOrders", 0L);
            fallback.put("totalRevenue", 0.0);
            fallback.put("totalUsers", 0L);
            
            return ResponseEntity.ok(ApiResponse.success("Success with errors", fallback));
        }
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
