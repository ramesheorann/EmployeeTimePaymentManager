package com.example.payroll.service;

import com.example.payroll.model.Employee;
import com.example.payroll.model.Payslip;
import com.example.payroll.model.TimeEntry;
import com.example.payroll.repository.EmployeeRepository;
import com.example.payroll.repository.PayslipRepository;
import com.example.payroll.repository.TimeEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AdminService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private TimeEntryRepository timeEntryRepository;
    
    @Autowired
    private PayslipRepository payslipRepository;
    
    //new
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public long getEmployeeCount() {
        return employeeRepository.count();
    }
    
    public List<TimeEntry> getPendingTimeEntries() {
        return timeEntryRepository.findByApprovedFalse();
    }
    
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }
    
    public void approveTimeEntry(Long id) {
        TimeEntry entry = timeEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Time entry not found"));
        entry.setApproved(true);
        timeEntryRepository.save(entry);
    }
    
    public List<Payslip> getAllPayslips() {
        return payslipRepository.findAll();
    }
    
    public void generatePayslipsForPeriod(LocalDate periodStart, LocalDate periodEnd) {
        List<Employee> employees = employeeRepository.findAll();
        
        for (Employee employee : employees) {
            List<TimeEntry> entries = timeEntryRepository
                    .findByEmployeeIdAndClockInBetweenAndApprovedTrue(
                            employee.getId(),
                            periodStart.atStartOfDay(),
                            periodEnd.atTime(23, 59, 59));
            
            BigDecimal totalHours = calculateTotalHours(entries);
            BigDecimal grossPay = totalHours.multiply(employee.getHourlyRate());
            BigDecimal taxAmount = calculateTax(grossPay);
            BigDecimal netPay = grossPay.subtract(taxAmount);
            
            Payslip payslip = new Payslip();
            payslip.setEmployee(employee);
            payslip.setPeriodStart(periodStart);
            payslip.setPeriodEnd(periodEnd);
            payslip.setGrossPay(grossPay);
            payslip.setTaxAmount(taxAmount);
            payslip.setNetPay(netPay);
            payslip.setPaymentDate(LocalDate.now());
            
            payslipRepository.save(payslip);
        }
    }
    
    private BigDecimal calculateTotalHours(List<TimeEntry> entries) {
        long totalMinutes = entries.stream()
                .mapToLong(e -> Duration.between(e.getClockIn(), e.getClockOut()).toMinutes())
                .sum();
        return BigDecimal.valueOf(totalMinutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calculateTax(BigDecimal grossPay) {
        // Simplified tax calculation
        return grossPay.multiply(BigDecimal.valueOf(0.2)); // 20% tax
    }
    //newly added
    public void saveEmployee(Employee employee) {
        // If password field is filled, encode it
        if (employee.getId() == null || (employee.getPassword() != null && !employee.getPassword().isEmpty())) {
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        } else {
            // Keep existing password
            String existingPassword = employeeRepository.findById(employee.getId())
                    .map(Employee::getPassword)
                    .orElse(null);
            employee.setPassword(existingPassword);
        }

        // Set role explicitly if new
        if (employee.getRole() == null || employee.getRole().isEmpty()) {
            employee.setRole("ROLE_EMPLOYEE");
        }

        employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
//newly added end
}