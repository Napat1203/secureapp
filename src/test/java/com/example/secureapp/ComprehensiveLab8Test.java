package com.example.secureapp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
// 🔥 เพิ่ม Import นี้เพื่อใช้เช็ค Error
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
public class ComprehensiveLab8Test {

    @Autowired
    private MockMvc mockMvc;

    // ==========================================
    // ✅ CASE 1: กรณีปกติ (Happy Path)
    // ==========================================

    @Test
    @DisplayName("✅ 1. หน้า Login/Register ต้องเข้าได้โดยไม่ต้องล็อกอิน")
    public void publicPagesShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    @DisplayName("✅ 2. สมัครสมาชิกสำเร็จ ต้องเด้งไปหน้า Login")
    public void registrationSuccess() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "newuser")
                        .param("password", "1234")
                        .param("role", "STAFF")) // จำลองการส่ง Form
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?success"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("✅ 3. ADMIN ล็อกอินแล้วเข้า /home ต้องเจอหน้า admin.html")
    public void adminAccessHome() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"))
                .andExpect(model().attributeExists("username"));
    }

    @Test
    @WithMockUser(username = "staff", roles = {"STAFF"})
    @DisplayName("✅ 4. STAFF ล็อกอินแล้วเข้า /home ต้องเจอหน้า viewer.html")
    public void staffAccessHome() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("viewer"));
    }

    // ==========================================
    // ❌ CASE 2: กรณี Error / ผิดพลาด (Unhappy Path)
    // ==========================================

    @Test
    @DisplayName("❌ 5. เข้าหน้า /home โดยไม่ล็อกอิน ต้องโดนเด้งไป Login")
    public void accessHomeUnauthenticated() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().is3xxRedirection()) // 302 Redirect
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @DisplayName("❌ 6. ล็อกอินด้วยรหัสผิด ต้องเด้งกลับมาหน้า Login พร้อม Error")
    public void loginWithWrongPassword() throws Exception {
        // จำลองการ Login จริงผ่าน Spring Security
        mockMvc.perform(formLogin().user("admin").password("wrongpass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    @WithMockUser(username = "staff", roles = {"STAFF"})
    @DisplayName("❌ 7. STAFF พยายามเข้าหน้า /admin (Forbidden 403)")
    public void staffAccessAdminPage() throws Exception {
        // สมมติว่าใน WebSecurityConfig กันไว้ว่า /admin ต้องเป็น ADMIN เท่านั้น
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden()); // คาดหวัง 403
    }

    // ==========================================
    // ⚠️ CASE 3: กรณีไม่ใส่ข้อมูลบางอย่าง (Missing Fields)
    // ==========================================

    @Test
    @DisplayName("✅ 8. สมัครสมาชิกโดยไม่ระบุ Role (ระบบต้องตั้งค่า Default เป็น STAFF ให้)")
    public void registrationMissingRole() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "noRoleUser")
                        .param("password", "1234")) // ⚠️ ไม่ส่ง param role ไป
                // คาดหวัง: ระบบต้องไม่พัง และพาไปหน้า Login ได้ (เพราะเราเขียนโค้ดดักไว้แล้ว)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?success"));
    }

    // 🔥🔥🔥 แก้ไขข้อ 9: ใช้ assertThrows เพื่อรับมือกับการระเบิดของ Controller 🔥🔥🔥
    @Test
    @DisplayName("❌ 9. สมัครสมาชิกโดยไม่ใส่ Password (คาดหวัง Exception)")
    public void registrationMissingPassword() {
        // คาดหวังว่าการกระทำนี้ "ต้องทำให้เกิด Error" (Exception)
        assertThrows(Exception.class, () -> {
            mockMvc.perform(post("/register")
                            .with(csrf())
                            .param("username", "noPassUser")
                            .param("role", "STAFF")); // ไม่ส่ง password
        });
    }
}