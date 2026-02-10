package com.example.secureapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WebsiteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    public void shouldRedirectGuestToLogin() throws Exception {
        mockMvc.perform(get("/home")) // ถ้าคนทั่วไปเข้า home ต้องเด้งไป login
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "boss", roles = {"ADMIN"})
    public void shouldAllowAdminAccess() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())        // แก้เป็น isOk()
                .andExpect(view().name("admin"));  // แก้เป็น view().name("admin")
    }

    @Test
    @WithMockUser(username = "staff", roles = {"STAFF"})
    public void shouldAllowStaffAccess() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())         // แก้เป็น isOk()
                .andExpect(view().name("viewer"));  // แก้เป็น view().name("viewer")
    }
}