package com.eventkonser.controller;

import com.eventkonser.model.Schedule;
import com.eventkonser.service.ScheduleService;
import com.eventkonser.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ScheduleController {
    
    private final ScheduleService scheduleService;
    
    /**
     * GET /api/schedules - Get all schedules
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Schedule>>> getAllSchedules() {
        return ResponseEntity.ok(ApiResponse.success("Success", scheduleService.getAllSchedules()));
    }
    
    /**
     * GET /api/schedules/event/{eventId} - Get schedules by event
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<List<Schedule>>> getSchedulesByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(ApiResponse.success("Success", scheduleService.getSchedulesByEvent(eventId)));
    }
    
    /**
     * GET /api/schedules/date/{date} - Get schedules by date
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<ApiResponse<List<Schedule>>> getSchedulesByDate(@PathVariable LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success("Success", scheduleService.getSchedulesByDate(date)));
    }
    
    /**
     * GET /api/schedules/{id} - Get schedule by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Schedule>> getScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Success", scheduleService.getScheduleById(id)));
    }
    
    /**
     * POST /api/schedules - Create new schedule (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<Schedule>> createSchedule(@RequestBody Schedule schedule) {
        return ResponseEntity.ok(ApiResponse.success("Jadwal berhasil dibuat", scheduleService.createSchedule(schedule)));
    }
    
    /**
     * PUT /api/schedules/{id} - Update schedule (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Schedule>> updateSchedule(
            @PathVariable Long id,
            @RequestBody Schedule scheduleDetails) {
        return ResponseEntity.ok(ApiResponse.success("Jadwal berhasil diupdate", scheduleService.updateSchedule(id, scheduleDetails)));
    }
    
    /**
     * DELETE /api/schedules/{id} - Delete schedule (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok(ApiResponse.success("Jadwal berhasil dihapus", null));
    }
}