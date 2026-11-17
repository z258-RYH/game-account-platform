package com.platform.dto;

/**
 * 注册请求体（用于接收前端传来的数据）
 */
public class RegisterRequest {
    private String username;   // 用户名
    private String password;  // 密码（明文，后端会加密后存储）
    private String phone;      // 手机号（可选）
    private String email;      // 邮箱（可选）

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
