package com.example.payroll.repository;

import com.example.payroll.model.TimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    List<TimeEntry> findByEmployeeId(Long employeeId);
    List<TimeEntry> findByEmployeeIdAndClockInBetween(Long employeeId, LocalDateTime start, LocalDateTime end);
    List<TimeEntry> findByApprovedFalse();
    Optional<TimeEntry> findFirstByEmployeeIdAndClockOutIsNullOrderByClockInDesc(Long employeeId);
    List<TimeEntry> findByEmployeeIdAndClockInBetweenAndApprovedTrue(Long employeeId, LocalDateTime start, LocalDateTime end);
}