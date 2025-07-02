package com.example.payroll;

import com.example.payroll.model.Employee;
import com.example.payroll.model.User;
import com.example.payroll.repository.EmployeeRepository;
import com.example.payroll.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;

@SpringBootApplication
public class PayrollApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayrollApplication.class, args);
    }
    
    @Bean
    public CommandLineRunner initData(UserRepository userRepository, 
                                    EmployeeRepository employeeRepository,
                                    PasswordEncoder passwordEncoder) {
        return args -> {
            // Create admin user(if not exist)
            if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123"));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);
            }
            
            // Creating a Demo employee (if not exist)
            if (userRepository.findByUsername("employee1").isEmpty()) {
            Employee employee = new Employee();
            employee.setUsername("employee1");
            employee.setPassword(passwordEncoder.encode("123456"));
            employee.setRole("ROLE_EMPLOYEE");
            employee.setFirstName("Ramesh");
            employee.setLastName("Sheoran");
            employee.setEmail("ramesheorann@gmail.com");
            employee.setPosition("Developer");
            employee.setHourlyRate(new BigDecimal("500"));
            employeeRepository.save(employee);
            }
        };
    }
}