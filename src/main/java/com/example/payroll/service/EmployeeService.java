package com.example.payroll.service;

import com.example.payroll.model.Employee;
import com.example.payroll.model.Payslip;
import com.example.payroll.model.TimeEntry;
import com.example.payroll.repository.EmployeeRepository;
import com.example.payroll.repository.PayslipRepository;
import com.example.payroll.repository.TimeEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class EmployeeService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private TimeEntryRepository timeEntryRepository;
    
    @Autowired
    private PayslipRepository payslipRepository;
    
    public Employee findByUsername(String username) {
        return employeeRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found"));
    }
    
    public void clockIn(String username) {
        Employee employee = findByUsername(username);
        TimeEntry entry = new TimeEntry();
        entry.setEmployee(employee);
        entry.setClockIn(LocalDateTime.now());
        timeEntryRepository.save(entry);
    }
    
    public void clockOut(String username) {
        Employee employee = findByUsername(username);
        TimeEntry entry = timeEntryRepository.findFirstByEmployeeIdAndClockOutIsNullOrderByClockInDesc(employee.getId())
                .orElseThrow(() -> new IllegalStateException("No active clock-in found"));
        entry.setClockOut(LocalDateTime.now());
        timeEntryRepository.save(entry);
    }
    
    public List<TimeEntry> getTimeEntriesForCurrentMonth(Long employeeId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);
        return timeEntryRepository.findByEmployeeIdAndClockInBetween(employeeId, start, end);
    }
    
    public List<Payslip> getPayslips(Long employeeId) {
        return payslipRepository.findByEmployeeId(employeeId);
    }
    
    public Payslip getPayslip(Long payslipId, String username) {
        Employee employee = findByUsername(username);
        return payslipRepository.findByIdAndEmployeeId(payslipId, employee.getId())
                .orElseThrow(() -> new RuntimeException("Payslip not found"));
    }
}