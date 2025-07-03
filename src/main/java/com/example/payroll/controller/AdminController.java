package com.example.payroll.controller;

import com.example.payroll.model.Employee;
import com.example.payroll.model.Payslip;
import com.example.payroll.model.TimeEntry;
import com.example.payroll.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("employeeCount", adminService.getEmployeeCount());
        model.addAttribute("pendingApprovals", adminService.getPendingTimeEntries());
        model.addAttribute("payslipsCount", adminService.getPayslipsCount());
        return "admin/dashboard";
    }
    
    @GetMapping("/employees")
    public String listEmployees(Model model) {
        model.addAttribute("employees", adminService.getAllEmployees());
        return "admin/employees";
    }
    
    @GetMapping("/employee/{id}")
    public String viewEmployee(@PathVariable Long id, Model model) {
        model.addAttribute("employee", adminService.getEmployeeById(id));
        return "admin/employee-detail";
    }
    
    @GetMapping("/timesheets")
    public String manageTimesheets(Model model) {
        model.addAttribute("pendingEntries", adminService.getPendingTimeEntries());
        return "admin/timesheets";
    }
    
    @PostMapping("/approve-time/{id}")
    public String approveTimeEntry(@PathVariable Long id) {
        adminService.approveTimeEntry(id);
        return "redirect:/admin/timesheets";
    }
    //NEW
    @PostMapping("/reject-time/{id}")
    public String deleteTimeEntry(@PathVariable Long id) {
        adminService.deleteTimeEntry(id);
        return "redirect:/admin/timesheets";
    }
    
    @PostMapping("/delete-payslip/{id}")
    public String deletePayslip(@PathVariable Long id) {
    	adminService.deletePayslip(id);
    	return "redirect:/admin/payslips";
    }
    //NEW END
    
    @GetMapping("/payslips")
    public String managePayslips(Model model) {
        model.addAttribute("payslips", adminService.getAllPayslips());
        return "admin/payslips";
    }
    
    @GetMapping("/generate-payslips")
    public String showGeneratePayslipsForm(Model model) {
        model.addAttribute("periodStart", LocalDate.now().withDayOfMonth(1));
        model.addAttribute("periodEnd", LocalDate.now());
        return "admin/generate-payslips";
    }
    
    @PostMapping("/generate-payslips")
    public String generatePayslips(@RequestParam LocalDate periodStart, 
                                  @RequestParam LocalDate periodEnd) {
        adminService.generatePayslipsForPeriod(periodStart, periodEnd);
        return "redirect:/admin/payslips";
    }
    //below are newly added 
    // Show form to add a new employee
    @GetMapping("/employee/new")
    public String showCreateEmployeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "admin/employee-form";
    }
    
    // Show form to update existing employee
    @GetMapping("/employee/edit/{id}")
    public String showEditEmployeeForm(@PathVariable Long id, Model model) {
        model.addAttribute("employee", adminService.getEmployeeById(id));
        return "admin/employee-form";
    }
    
    @PostMapping("/employee/save")
    public String saveEmployee(@ModelAttribute("employee") Employee employee) {
        adminService.saveEmployee(employee);
        return "redirect:/admin/employees";
    }

    @GetMapping("/employee/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        adminService.deleteEmployee(id);
        return "redirect:/admin/employees";
    }
    //adding new new 
    @GetMapping("/payslip/{id}")
    public String viewPayslip(@PathVariable Long id, Model model, Principal principal) {
        Payslip payslip = adminService.getPayslip(id);
        model.addAttribute("payslip", payslip);
        return "admin/payslip-detail";
    }
    
}