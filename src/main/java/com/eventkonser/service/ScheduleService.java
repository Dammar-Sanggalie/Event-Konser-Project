package com.eventkonser.service;

import com.eventkonser.model.Schedule;
import com.eventkonser.repository.ScheduleRepository;
import com.eventkonser.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    
    private final ScheduleRepository scheduleRepository;
    
    @Transactional(readOnly = true)
    public List<Schedule> getSchedulesByEvent(Long eventId) {
        return scheduleRepository.findByEvent_IdEventOrderByTanggalAscJamMulaiAsc(eventId);
    }
    
    @Transactional(readOnly = true)
    public List<Schedule> getSchedulesByDate(LocalDate date) {
        return scheduleRepository.findByTanggal(date);
    }
    
    @Transactional(readOnly = true)
    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Jadwal tidak ditemukan dengan ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<Schedule> getTodaySchedules() {
        return scheduleRepository.findByTanggalOrderByJamMulaiAsc(LocalDate.now());
    }
    
    @Transactional
    public Schedule createSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }
    
    @Transactional
    public Schedule updateSchedule(Long id, Schedule scheduleDetails) {
        Schedule schedule = getScheduleById(id);
        schedule.setTanggal(scheduleDetails.getTanggal());
        schedule.setJamMulai(scheduleDetails.getJamMulai());
        schedule.setJamSelesai(scheduleDetails.getJamSelesai());
        schedule.setKeterangan(scheduleDetails.getKeterangan());
        return scheduleRepository.save(schedule);
    }
    
    @Transactional
    public void deleteSchedule(Long id) {
        Schedule schedule = getScheduleById(id);
        scheduleRepository.delete(schedule);
    }
    
    @Transactional(readOnly = true)
    public List<Schedule> getUpcomingSchedules() {
        return scheduleRepository.findByTanggalOrderByJamMulaiAsc(LocalDate.now());
    }
    
    @Transactional(readOnly = true)
    public List<Schedule> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Schedule> result = scheduleRepository.findByTanggal(startDate);
        LocalDate current = startDate.plusDays(1);
        while (current.isBefore(endDate) || current.isEqual(endDate)) {
            result.addAll(scheduleRepository.findByTanggal(current));
            current = current.plusDays(1);
        }
        return result;
    }
}