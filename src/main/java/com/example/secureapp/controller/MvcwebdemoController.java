package com.example.secureapp.controller; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; 
import org.springframework.validation.BindingResult; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid; 

import com.example.mvcwebdemoas.model.RegistrationForm; 

@Controller
public class MvcwebdemoController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm()); 
        return "registration";
    }

    @PostMapping("/register")
    public String register(@Valid RegistrationForm registrationForm, 
                         BindingResult bindingResult, 
                         Model model) {
        
        if (bindingResult.hasErrors()) {
            return "registration";
        }

        model.addAttribute("firstName", registrationForm.getFirstName());
        model.addAttribute("lastName", registrationForm.getLastName());
        model.addAttribute("country", registrationForm.getCountry());
        model.addAttribute("dob", registrationForm.getDob());
        model.addAttribute("email", registrationForm.getEmail());
        
        return "success";
    }
}