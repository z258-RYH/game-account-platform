package com.platform.dto;

/**
 * 登录请求体
 * 支持用 用户名 或 手机号 登录
 */
public class LoginRequest {
    private String username;   // 用户名 或 手机号
    private String password;   // 密码（明文）

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
