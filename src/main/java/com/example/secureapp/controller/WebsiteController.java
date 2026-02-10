package com.example.secureapp.controller;

// Import คลาสที่จำเป็นทั้งหมด (ครบถ้วน)
import com.example.secureapp.model.User;
import com.example.secureapp.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebsiteController {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ------------------- ส่วน Login -------------------
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // ------------------- ส่วน Register -------------------
    @GetMapping("/register")
    public String registerForm(Model model) {
        // ส่ง User เปล่าๆ ไปให้หน้าเว็บกรอกข้อมูล (ต้องใช้ Constructor เปล่าใน User.java)
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        // 1. เข้ารหัสรหัสผ่าน
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // 2. จัดการเรื่อง Role (ถ้าไม่ได้เลือกมา ให้เป็น STAFF)
        if (user.getRole() != null && !user.getRole().isEmpty()) {
            // ถ้าหน้าเว็บส่งมาแค่ "ADMIN" เราเติม "ROLE_" ข้างหน้าให้
            if (!user.getRole().startsWith("ROLE_")) {
                user.setRole("ROLE_" + user.getRole());
            }
        } else {
            user.setRole("ROLE_STAFF");
        }

        // 3. เรียก method createUser ใน Service (ที่เคยหายไป)
        userDetailsService.createUser(user);
        
        return "redirect:/login?success";
    }

    // ------------------- ส่วน Home (แยกยศ) -------------------
    @GetMapping("/home")
    public String homepage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        model.addAttribute("username", username);

        // ดึง Role ออกมาเช็ค
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        // ถ้าเป็น Admin ให้เปิดหน้า admin.html
        if (role.contains("ADMIN")) {
            return "admin"; 
        } else {
            return "viewer"; // ถ้าเป็น Staff หรืออื่นๆ ไปหน้า viewer.html
        }
    }
}