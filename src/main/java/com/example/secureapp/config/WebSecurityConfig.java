package com.example.secureapp.config;

import com.example.secureapp.service.CustomUserDetailsService; // 1. ต้อง Import Service ของเพื่อนมา
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    // 2. ต้องเรียกใช้ CustomUserDetailsService (ที่เพื่อนสร้างไว้เก็บ User ใน Map)
    private final CustomUserDetailsService userDetailsService;

    public WebSecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/register", "/login", "/css/**", "/js/**").permitAll()
                .requestMatchers("/admin").hasRole("ADMIN")
                .requestMatchers("/viewer").hasRole("STAFF") // เช็ค Role STAFF
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true) // Login ผ่านให้ไป /home
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        
        return http.build();
    }

    // 3. (ส่วนที่หายไป) ตั้งค่า AuthenticationManager ให้รู้จัก Service และการเข้ารหัส
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
            
        authenticationManagerBuilder
            .userDetailsService(userDetailsService) // บอกให้ใช้ Service ของเรา
            .passwordEncoder(passwordEncoder());    // บอกให้ใช้ BCrypt
            
        return authenticationManagerBuilder.build();
    }

    // 4. (ส่วนที่หายไป) สร้าง Bean PasswordEncoder (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}