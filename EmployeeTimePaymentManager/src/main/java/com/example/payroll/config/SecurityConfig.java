package com.example.payroll.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http                                                                        //can disable default security
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/","/images/**", "/login", "/css/**", "/js/**", "/h2-console/**").permitAll()   //permit these all url as no security needed
                .requestMatchers("/employee/**").hasAuthority("ROLE_EMPLOYEE")  // Changed to hasAuthority
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")       // Changed to hasAuthority
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard")
                .successHandler((request, response, authentication) -> {
                    // Redirect based on role
                    if (authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        response.sendRedirect("/admin/dashboard");
                    } else {
                        response.sendRedirect("/employee/dashboard");
                    }
                })
                .permitAll()
            )
            .logout(logout -> logout
            	    .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")) // allow GET logout
            	    .logoutSuccessUrl("/login?logout")
            	    .invalidateHttpSession(true)
            	    .clearAuthentication(true)
            	    .permitAll()
            	)
            .headers(headers -> headers
                .frameOptions().disable()  // For H2 console
            );
        
        return http.build();  //return whole security filters bunch HttpSecurity's object
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}