package com.MyApp.DoctorConsultantApp.config;

import com.MyApp.DoctorConsultantApp.dto.Role;
import com.MyApp.DoctorConsultantApp.model.User;
import com.MyApp.DoctorConsultantApp.repository.UserRepository;
import com.MyApp.DoctorConsultantApp.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private AdminService adminService;

    @Bean
    public CommandLineRunner insertAdmin(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail("admin@gmail.com")) {
                User admin = new User();
                admin.setFullName("Admin");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(passwordEncoder.encode("admin@123")); // 💡 HASHED!
                admin.setPhone("9059021804");
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(adminService).passwordEncoder(new BCryptPasswordEncoder());
        return authBuilder.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/doctor/register",
                                "/api/login",
                                "/api/doctor/search",
                                "/api/home/doctor/**",
                                "/api/user/register",
                                "/api/user/profile/**",
                                "/api/admin/ping",
                                "/api/doctor/profile/**",
                                "/api/user/change-password/**",
                                "/api/appointments/doctor/**",
                                "/api/appointments/user/**",
                                "/api/appointments/approve/**",
                                "/api/appointments/complete/**",
                                "/api/payments/pay/**",
                                "/api/appointments/book",
                                "/api/notifications/**",
                                "/api/admin/appointments",
                                "/api/admin/pending",
                                "/api/admin/approve-doctor/**",
                                "/api/appointments/complete/**",
                                "/api/admin/dashboard"
                        ).permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
