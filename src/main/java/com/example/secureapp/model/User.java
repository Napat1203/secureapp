package com.example.secureapp.model;

public class User {
    private String username;
    private String password;
    private String role;

    // ✅ 1. Constructor เปล่า (จำเป็นมาก! เพื่อให้ Controller สร้าง new User() ได้)
    public User() {
    }

    // ✅ 2. Constructor แบบเต็ม (เผื่อไว้ใช้)
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // ✅ 3. Getters และ Setters (ต้องมีครบ ไม่งั้น Controller ดึงค่าไม่ได้)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}