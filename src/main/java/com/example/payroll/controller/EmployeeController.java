package com.example.payroll.controller;

import com.example.payroll.model.Employee;
import com.example.payroll.model.Payslip;
import com.example.payroll.model.TimeEntry;
import com.example.payroll.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;
    
    @GetMapping("/dashboard")
    public String employeeDashboard(Model model, Principal principal) {
        Employee employee = employeeService.findByUsername(principal.getName());
        model.addAttribute("employee", employee);
        
        // Calculate next Friday
        LocalDate today = LocalDate.now();
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        model.addAttribute("nextPayday", lastDayOfMonth);
        return "employee/dashboard";
    }
    
    @GetMapping("/timesheet")
    public String viewTimesheet(Model model, Principal principal) {
        Employee employee = employeeService.findByUsername(principal.getName());
        List<TimeEntry> entries = employeeService.getTimeEntriesForCurrentMonth(employee.getId());
        model.addAttribute("entries", entries);
        return "employee/timesheet";
    }
    
    @PostMapping("/clock-in")
    public String clockIn(Principal principal) {
        employeeService.clockIn(principal.getName());
        return "redirect:/employee/timesheet";
    }
    
    @PostMapping("/clock-out")
    public String clockOut(Principal principal) {
        employeeService.clockOut(principal.getName());
        return "redirect:/employee/timesheet";
    }
    
    @GetMapping("/payslips")
    public String viewPayslips(Model model, Principal principal) {
        Employee employee = employeeService.findByUsername(principal.getName());
        List<Payslip> payslips = employeeService.getPayslips(employee.getId());
        model.addAttribute("payslips", payslips);
        return "employee/payslips";
    }
    
    @GetMapping("/payslip/{id}")
    public String viewPayslip(@PathVariable Long id, Model model, Principal principal) {
        Payslip payslip = employeeService.getPayslip(id, principal.getName());
        model.addAttribute("payslip", payslip);
        return "employee/payslip-detail";
    }
}