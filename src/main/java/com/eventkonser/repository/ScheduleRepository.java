package com.eventkonser.repository;

import com.eventkonser.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    
    // Find by event
    List<Schedule> findByEvent_IdEventOrderByTanggalAscJamMulaiAsc(Long eventId);
    
    // Find by date
    List<Schedule> findByTanggal(LocalDate tanggal);
    
    // Find schedules for today
    List<Schedule> findByTanggalOrderByJamMulaiAsc(LocalDate today);
    
    // Check schedule conflict for venue
    @Query("SELECT s FROM Schedule s WHERE s.event.venue.idVenue = :venueId " +
           "AND s.tanggal = :tanggal " +
           "AND ((s.jamMulai <= :jamMulai AND s.jamSelesai > :jamMulai) " +
           "OR (s.jamMulai < :jamSelesai AND s.jamSelesai >= :jamSelesai))")
    List<Schedule> findConflictingSchedules(@Param("venueId") Long venueId,
                                             @Param("tanggal") LocalDate tanggal,
                                             @Param("jamMulai") java.time.LocalTime jamMulai,
                                             @Param("jamSelesai") java.time.LocalTime jamSelesai);
                                             
    // Find all schedules with event data
    @Query("SELECT s FROM Schedule s LEFT JOIN FETCH s.event e LEFT JOIN FETCH e.venue ORDER BY s.tanggal, s.jamMulai")
    List<Schedule> findAllWithEvent();
}