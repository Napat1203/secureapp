package com.example.secureapp.service;

// Import User จากไฟล์ที่เราเพิ่งสร้าง (เช็ค package ให้ตรง)
import com.example.secureapp.model.User; 

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    // ใช้ Map เก็บข้อมูล User (แทน Database)
    private final Map<String, User> users = new HashMap<>();

    // ✅ Method นี้ต้องมี! เพื่อให้ Controller เรียกใช้ตอนสมัครสมาชิก
    public void createUser(User user) {
        users.put(user.getUsername(), user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        
        // แปลงข้อมูล User ของเรา ให้เป็นแบบที่ Spring Security เข้าใจ
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                // ตรงนี้สำคัญ: ตัด ROLE_ ออกก่อน แล้วให้ builder เติมให้ (กันซ้ำ)
                .roles(user.getRole().replace("ROLE_", "")) 
                .build();
    }
}