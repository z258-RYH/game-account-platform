package com.platform.dto;

/**
 * 登录成功后的返回数据
 * 不返回密码，只返回 token 和用户基本信息
 */
public class LoginResponse {
    private String token;      // JWT 令牌，后续请求需携带
    private Long userId;
    private String username;
    private Boolean verified;  // 是否已实名认证
    private String role;       // 用户角色

    public LoginResponse(String token, Long userId, String username, Boolean verified, String role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.verified = verified;
        this.role = role;
    }

    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public Boolean getVerified() { return verified; }
    public String getRole() { return role; }
}
