package com.example.payroll.repository;

import com.example.payroll.model.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {
    List<Payslip> findByEmployeeId(Long employeeId);
    List<Payslip> findByPaymentDateBetween(LocalDate start, LocalDate end);
    Optional<Payslip> findByIdAndEmployeeId(Long id, Long employeeId);
}