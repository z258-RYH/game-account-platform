package com.platform.dto;

/**
 * 更新个人资料请求体
 */
public class UpdateProfileRequest {
    private String username;     // 用户名
    private String email;        // 邮箱
    private String avatarUrl;    // 头像URL

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}
