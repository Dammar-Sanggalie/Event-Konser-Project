package com.eventkonser.controller;

import com.eventkonser.model.Schedule;
import com.eventkonser.service.ScheduleService;
import com.eventkonser.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
     * POST /api/schedules - Create new schedule (Admin only)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Schedule>> createSchedule(@RequestBody Schedule schedule) {
        return ResponseEntity.ok(ApiResponse.success("Jadwal berhasil dibuat", scheduleService.createSchedule(schedule)));
    }
    
    /**
     * PUT /api/schedules/{id} - Update schedule (Admin only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Schedule>> updateSchedule(
            @PathVariable Long id,
            @RequestBody Schedule scheduleDetails) {
        return ResponseEntity.ok(ApiResponse.success("Jadwal berhasil diupdate", scheduleService.updateSchedule(id, scheduleDetails)));
    }
    
    /**
     * DELETE /api/schedules/{id} - Delete schedule (Admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok(ApiResponse.success("Jadwal berhasil dihapus", null));
    }
}