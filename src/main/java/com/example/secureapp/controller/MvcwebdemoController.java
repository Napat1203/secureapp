package com.example.secureapp.controller; 

import com.example.secureapp.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MvcwebdemoController {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, 
                               @RequestParam String password, 
                               @RequestParam String role) {
        userDetailsService.registerUser(username, password, role);
        return "redirect:/login"; 
    }

    @GetMapping("/home")
    public String home(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        String role = authentication.getAuthorities().toString();
        
        if (role.contains("ROLE_ADMIN")) {
            return "admin"; // ถ้าเป็น Admin ส่งไปหน้า admin.html
        } else if (role.contains("ROLE_STAFF")) {
            return "viewer"; // ถ้าเป็น Staff ส่งไปหน้า viewer.html
        }
        
        return "index";
    }
}